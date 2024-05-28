// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import com.upthinkexperts.common.db.UploadJobDB;
import org.jdbi.v3.core.Handle;
import java.util.Iterator;
import java.util.Optional;
import com.upthinkexperts.common.domain.QBankDoc;
import com.amazonaws.services.s3.AmazonS3;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.domain.QBankQuestion;
import java.util.Map;
import com.upthinkexperts.common.parsing.DocPart;
import java.util.HashMap;
import com.upthinkexperts.common.db.QBankDB;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.UUID;
import com.upthinkexperts.common.domain.QBankDBQuestion;
import java.util.List;
import com.upthinkexperts.common.util.QBankUtil;
import com.upthinkexperts.common.parsing.QBankDocConverter;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import com.upthinkexperts.common.parsing.QBankParser;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.yojito.minima.logging.MinimaLogger;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.parsing.ParsedDoc;
import com.upthinkexperts.common.domain.UploadJob;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import com.upthinkexperts.common.domain.JobFile;

public class QBankFileProcessor implements Runnable
{
    private JobFile qbank;
    private ZipEntry questionDocZipEntry;
    private ZipFile zipFile;
    private final UploadJob uploadJob;
    private ParsedDoc qBankDoc;
    private final CmsCommonConfig cmsConfig;
    private String tempFilePath;
    private final QBankBatch qBankBatch;
    private static final MinimaLogger LOGGER;
    
    public QBankFileProcessor(final JobFile qbank, final UploadJob uploadJob, final CmsCommonConfig context, final QBankBatch qBankBatch, final ZipEntry questionDoc) {
        this.qbank = qbank;
        this.uploadJob = uploadJob;
        this.cmsConfig = context;
        this.qBankBatch = qBankBatch;
        this.questionDocZipEntry = questionDoc;
    }
    
    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        long parseTime = -1L;
        long endTime = -1L;
        long uploadTime = -1L;
        long startUploadTime = -1L;
        long parseStartTime = -1L;
        String docName = null;
        String docId = null;
        final String equationTempPath = String.format("%s/%s", this.tempFilePath, "olebin");
        final String imagesTempPath = String.format("%s/%s", this.tempFilePath, "images");
        final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        final String bucket = this.cmsConfig.getBucket();
        try {
            QBankFileProcessor.LOGGER.info("Processing JobFile  = %s", new Object[] { this.qbank.getDocId() });
            docName = this.qbank.getDocId();
            docId = this.qbank.getDocId();
            final QBankParser qBankParser = new QBankParser();
            parseStartTime = System.currentTimeMillis();
            try {
                this.qBankDoc = QBankParser.parse(docName, true, ParsingSpecs.QBANK, this.zipFile.getInputStream(this.questionDocZipEntry), equationTempPath, imagesTempPath);
                this.qbank = this.qbank.parsed();
                QBankFileProcessor.LOGGER.info("Parsed Doc %s %s", new Object[] { docName, this.qbank.getDocFile() });
            }
            catch (final Exception e) {
                QBankFileProcessor.LOGGER.warn((Throwable)e, "File %s Doc Id %s : Question Doc Parsing Error", new Object[] { this.qbank.getDocFile(), this.qbank.getDocId() });
                this.qBankBatch.registerParsingFailure(docName, docId);
                this.updateFileForError(e, this.qbank, JobFile.JobFileErrorCodes.QUESTION_FILE_PARSE_ERROR);
                return;
            }
            parseTime = System.currentTimeMillis();
            final QBankDocConverter converter = new QBankDocConverter();
            final QBankDoc qBankKnowldgeDoc = converter.convert(docId, this.qBankDoc, this.uploadJob.getTenantKey());
            docId = qBankKnowldgeDoc.getDocId();
            QBankFileProcessor.LOGGER.debug("Retrieved real DocID = %s", new Object[] { docId });
            this.qBankBatch.registerFileDocId(this.questionDocZipEntry.getName(), docId);
            this.qBankDoc = this.qBankDoc.withNewDocId(docId);
            final String finalizedDocId = docId;
            Database.doWithinTx(this.cmsConfig.getDb(), handle -> {
                QBankUtil.upsertRootDoc(handle, qBankKnowldgeDoc);
                final List<QBankDBQuestion> dbQuestions = qBankKnowldgeDoc.getQuestions().stream().map(q -> {
                    final String uuid = UUID.randomUUID().toString();
                    final QBankDBQuestion dbQuestion = QBankUtil.toDBQuestion(handle, q, uuid, this.uploadJob.getUuid());
                    return dbQuestion;
                }).collect((Collector<? super Object, ?, List<QBankDBQuestion>>)Collectors.toList());
                final ArrayList<Object> inserts = new ArrayList<Object>();
                final ArrayList<Pair<QBankDBQuestion, QBankDBQuestion>> updates = new ArrayList<Pair<QBankDBQuestion, QBankDBQuestion>>();
                dbQuestions.forEach(q -> {
                    this.qBankBatch.addQuestion(q.getDocId(), q.getId());
                    final Optional<QBankDBQuestion> dbQuestion2 = QBankDB.loadQuestionWithoutDeletionCheck(handle, q.getId(), q.getDocId(), q.getTenantKey(), q.getSubjectId(), q.getTopicId(), q.getSubTopicId());
                    if (dbQuestion2.isPresent()) {
                        updates.add(Pair.of((Object)q, (Object)dbQuestion2.get()));
                    }
                    else {
                        inserts.add(q);
                    }
                    return;
                });
                final HashMap<Pair<String, Integer>, List<DocPart.DocRun>> imageMap = new HashMap<Pair<String, Integer>, List<DocPart.DocRun>>();
                inserts.forEach(q -> {
                    QBankFileProcessor.LOGGER.debug("Inserting question - %s.%d -%s", new Object[] { q.getDocId(), q.getId(), q.toStringPretty() });
                    QBankDB.insertQuestion(handle, q);
                    final Map<Pair<String, Integer>, List<DocPart.DocRun>> _imageMap2 = QBankUtil.retrieveImages(qBankKnowldgeDoc, q);
                    imageMap.putAll(_imageMap2);
                    QBankFileProcessor.LOGGER.debug("For Question %s %d images - %s", new Object[] { q.getDocId(), q.getId(), _imageMap2 });
                    final QBankQuestion parsedQDoc2 = qBankKnowldgeDoc.getQuestions().stream().filter(pq -> pq.getId() == q.getId() && pq.getDocId().equals(q.getDocId())).findFirst().get();
                    QBankUtil.uploadQuestionContent(s3Client, bucket, parsedQDoc2, this.uploadJob.getUuid(), q.getqDocKey());
                    this.qBankBatch.addQuestionDocKey(q.getDocId(), q.getId(), q.getqDocKey());
                    return;
                });
                updates.iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final Pair update = iterator.next();
                    final QBankDBQuestion oldQuestion = (QBankDBQuestion)update.getRight();
                    final QBankDBQuestion newQuestion = (QBankDBQuestion)update.getLeft();
                    QBankFileProcessor.LOGGER.debug("Updating question - %s.%d", new Object[] { newQuestion.getDocId(), newQuestion.getId() });
                    QBankDB.updateQuestion(handle, newQuestion);
                    final Map<Pair<String, Integer>, List<DocPart.DocRun>> _imageMap = QBankUtil.retrieveImages(qBankKnowldgeDoc, newQuestion);
                    imageMap.putAll((Map<?, ?>)_imageMap);
                    QBankFileProcessor.LOGGER.debug("For Question %s %d images - %s", new Object[] { newQuestion.getDocId(), newQuestion.getId(), _imageMap });
                    final QBankQuestion parsedQDoc = qBankKnowldgeDoc.getQuestions().stream().filter(pq -> pq.getId() == newQuestion.getId() && pq.getDocId().equals(newQuestion.getDocId())).findFirst().get();
                    QBankUtil.uploadQuestionContent(s3Client, bucket, parsedQDoc, this.uploadJob.getUuid(), oldQuestion.getqDocKey());
                    this.qBankBatch.addQuestionDocKey(newQuestion.getDocId(), newQuestion.getId(), oldQuestion.getqDocKey());
                }
                this.qBankBatch.addImageMap(finalizedDocId, imageMap);
                return;
            });
            final boolean isUpdate = (boolean)Database.withinTx(this.cmsConfig.getDb(), handle -> {
                this.upsertDoc(handle, qBankKnowldgeDoc);
                this._updateFile(handle, this.qbank);
                return Boolean.valueOf(false);
            });
            startUploadTime = System.currentTimeMillis();
            this.uploadDocFiles();
            uploadTime = System.currentTimeMillis();
        }
        catch (final Exception e2) {
            QBankFileProcessor.LOGGER.error((Throwable)e2, "Error while processing docFile for docId=%s", new Object[] { docId });
            this.updateFileForError(e2, this.qbank);
        }
        finally {
            endTime = System.currentTimeMillis();
            QBankFileProcessor.LOGGER.info("Total Time for %s = %d, withoutUpload = %d, ParsingTime= %d, ConvertTime=%d, start=%d, parse=%d, uploadTime=%d", new Object[] { docId, endTime - startTime, endTime - startUploadTime, parseTime - parseStartTime, parseTime - startUploadTime, startTime, parseTime, uploadTime - startUploadTime });
        }
    }
    
    private void upsertDoc(final Handle handle, final QBankDoc qBankKnowldgeDoc) {
    }
    
    private void updateFileForError(final Exception e, final JobFile jobFile) {
        this.updateFileForError(e, jobFile, JobFile.JobFileErrorCodes.UNKNOWN);
    }
    
    private void updateFileForError(final Exception e, final JobFile jobFile, final JobFile.JobFileErrorCodes code) {
        String error = e.getMessage();
        if (error == null) {
            error = "NPE|" + code.name();
        }
        else if (error.length() > 250) {
            error = error.substring(0, 254);
        }
        final JobFile errorJobFile = jobFile.error(error, code);
        this.updateFileForError(errorJobFile);
    }
    
    private void updateFileForError(final JobFile jobFile) {
        Database.withinTx(this.cmsConfig.getDb(), handle -> {
            QBankFileProcessor.LOGGER.info("Updating JobFile Error %s", new Object[] { jobFile.toStringPretty() });
            return UploadJobDB.updateJobFileError(handle, jobFile);
        });
    }
    
    private int _updateFile(final Handle handle, final JobFile qbank) {
        try {
            QBankFileProcessor.LOGGER.info("Updating JobFile %s", new Object[] { qbank.toStringPretty() });
            UploadJobDB.updateJobFileStatus(handle, qbank);
            return UploadJobDB.updateJobFileStatus(handle, qbank);
        }
        catch (final Exception e) {
            QBankFileProcessor.LOGGER.error((Throwable)e, "Error while updating file status", new Object[0]);
            throw new RuntimeException(e);
        }
    }
    
    private void uploadDocFiles() {
    }
    
    public void setZipFile(final ZipFile zipFile) {
        this.zipFile = zipFile;
    }
    
    public void setTempFilePath(final String absolutePath) {
        this.tempFilePath = absolutePath;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankFileProcessor.class);
    }
}
