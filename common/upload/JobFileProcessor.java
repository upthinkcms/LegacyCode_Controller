// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import com.upthinkexperts.common.db.DocDB;
import org.jdbi.v3.core.Handle;
import com.upthinkexperts.common.db.UploadJobDB;
import java.net.URISyntaxException;
import com.upthinkexperts.common.util.IndexCommand;
import com.amazonaws.services.s3.AmazonS3;
import java.io.IOException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.upthinkexperts.common.parsing.ParsedDoc;
import com.upthinkexperts.common.parsing.DocParser;
import java.util.Collection;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.parsing.SubjectDocConverter;
import com.upthinkexperts.common.parsing.PoiDocParser;
import com.upthinkexperts.common.util.IndexingBatch;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import com.upthinkexperts.common.domain.UploadJob;
import java.util.zip.ZipFile;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import java.util.zip.ZipEntry;
import com.upthinkexperts.common.domain.JobFile;
import com.yojito.minima.logging.MinimaLogger;

public class JobFileProcessor implements Runnable
{
    private static final MinimaLogger LOGGER;
    private JobFile question;
    private JobFile answer;
    private ZipEntry questionDoc;
    private ZipEntry answerDoc;
    private ParsingSpec parsingSpec;
    private ZipFile zipFile;
    private final UploadJob uploadJob;
    private KnowledgeDoc knowledgeDoc;
    private final CmsCommonConfig cmsConfig;
    private IndexingBatch indexingBatch;
    private boolean isReIndexing;
    private boolean isUpdate;
    private String tempFilePath;
    
    public JobFileProcessor(final CmsCommonConfig context, final UploadJob uploadJob, final JobFile question, final JobFile answer, final ZipEntry questionDoc, final ZipEntry answerDoc) {
        this.isReIndexing = false;
        this.cmsConfig = context;
        this.uploadJob = uploadJob;
        this.question = question;
        this.answer = answer;
        this.answerDoc = answerDoc;
        this.questionDoc = questionDoc;
    }
    
    public void setReIndexing(final boolean reIndexing) {
        this.isReIndexing = reIndexing;
    }
    
    public void setParsingSpec(final ParsingSpec parsingSpec) {
        this.parsingSpec = parsingSpec;
    }
    
    public void setIndexingBatch(final IndexingBatch indexingBatch) {
        this.indexingBatch = indexingBatch;
    }
    
    public void setZipFile(final ZipFile zipFile) {
        this.zipFile = zipFile;
    }
    
    public void setTempFilePath(final String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }
    
    @Override
    public void run() {
        final long startTime = System.currentTimeMillis();
        long parseTime = -1L;
        long endTime = -1L;
        long uploadTime = -1L;
        long startUploadTime = -1L;
        long parseStartTime = -1L;
        String docId = null;
        final String equationTempPath = String.format("%s/%s", this.tempFilePath, "olebin");
        final String imagesTempPath = String.format("%s/%s", this.tempFilePath, "images");
        try {
            if (this.question == null) {
                docId = this.answer.getDocId();
            }
            else {
                docId = this.question.getDocId();
            }
            JobFileProcessor.LOGGER.info("Processing JobFiles for docId = %s Q = %s, A = %s", new Object[] { docId, this.question, this.answer });
            final DocParser docParser = new PoiDocParser();
            if (this.question == null) {
                this.answer = this.answer.error("QUESTION_FILE_MISSING", JobFile.JobFileErrorCodes.QUESTION_FILE_MISSING);
                JobFileProcessor.LOGGER.warn("File %s Doc Id %s : Question Doc file is missing", new Object[] { this.answer.getDocFile(), this.answer.getDocId() });
                this.updateFileForError(this.answer);
                endTime = System.currentTimeMillis();
            }
            else if (this.answer == null) {
                this.question = this.question.error("ANSWER_FILE_MISSING", JobFile.JobFileErrorCodes.ANSWER_FILE_MISSING);
                JobFileProcessor.LOGGER.warn("File %s Doc Id %s : Question Doc file is missing", new Object[] { this.question.getDocFile(), this.question.getDocId() });
                this.updateFileForError(this.question);
                endTime = System.currentTimeMillis();
            }
            else {
                ParsedDoc parsedQuestionDoc = null;
                ParsedDoc parsedAnswerDoc = null;
                parseStartTime = System.currentTimeMillis();
                boolean questionParsed = false;
                boolean answerParsed = false;
                try {
                    parsedQuestionDoc = docParser.parse(docId, true, this.parsingSpec, this.zipFile.getInputStream(this.questionDoc), equationTempPath, imagesTempPath);
                    this.question = this.question.parsed();
                    questionParsed = true;
                    JobFileProcessor.LOGGER.info("Parsed Question %s %s", new Object[] { this.question.getDocId(), this.question.getDocFile() });
                }
                catch (final Exception e) {
                    JobFileProcessor.LOGGER.warn((Throwable)e, "File %s Doc Id %s : Question Doc Parsing Error", new Object[] { this.question.getDocFile(), this.question.getDocId() });
                    this.updateFileForError(e, this.question, JobFile.JobFileErrorCodes.QUESTION_FILE_PARSE_ERROR);
                    this.updateFileForError(e, this.answer, JobFile.JobFileErrorCodes.QUESTION_FILE_PARSE_ERROR);
                }
                try {
                    parsedAnswerDoc = docParser.parse(docId, false, this.parsingSpec, this.zipFile.getInputStream(this.answerDoc), equationTempPath, imagesTempPath);
                    this.answer = this.answer.parsed();
                    answerParsed = true;
                    JobFileProcessor.LOGGER.info("Parsed Answer %s %s", new Object[] { this.answer.getDocId(), this.answer.getDocFile() });
                }
                catch (final Exception e) {
                    JobFileProcessor.LOGGER.warn((Throwable)e, "File %s Doc Id %s : Answer Doc Parsing Error", new Object[] { this.answer.getDocFile(), this.answer.getDocId() });
                    this.updateFileForError(e, this.answer, JobFile.JobFileErrorCodes.ANSWER_FILE_PARSE_ERROR);
                    this.updateFileForError(e, this.question, JobFile.JobFileErrorCodes.ANSWER_FILE_PARSE_ERROR);
                }
                parseTime = System.currentTimeMillis();
                if (questionParsed && answerParsed) {
                    final SubjectDocConverter converter = new SubjectDocConverter(this.uploadJob.getSubject(), this.uploadJob.getCategory(), this.uploadJob.getSubcategory(), this.uploadJob.getDifficulty());
                    this.knowledgeDoc = converter.convert(docId, parsedQuestionDoc, parsedAnswerDoc);
                    this.isUpdate = (boolean)Database.withinTx(this.cmsConfig.getDb(), handle -> {
                        final boolean isUpdate1 = this._upsertDoc(handle, this.knowledgeDoc);
                        this._updateFile(handle, this.question, this.answer);
                        return Boolean.valueOf(isUpdate1);
                    });
                    this.addIndexBatch(docId, parsedQuestionDoc, parsedAnswerDoc);
                    startUploadTime = System.currentTimeMillis();
                    this.uploadDocFiles(parsedQuestionDoc.getImages(), parsedAnswerDoc.getImages());
                    uploadTime = System.currentTimeMillis();
                }
            }
        }
        catch (final Exception e2) {
            JobFileProcessor.LOGGER.error((Throwable)e2, "Error while processing docFile for docId=%s", new Object[] { docId });
            if (this.question != null) {
                this.updateFileForError(e2, this.question);
            }
            if (this.answer != null) {
                this.updateFileForError(e2, this.answer);
            }
        }
        finally {
            endTime = System.currentTimeMillis();
            JobFileProcessor.LOGGER.info("Total Time for %s = %d, withoutUpload = %d, ParsingTime= %d, ConvertTime=%d, start=%d, parse=%d, uploadTime=%d", new Object[] { docId, endTime - startTime, endTime - startUploadTime, parseTime - parseStartTime, parseTime - startUploadTime, startTime, parseTime, uploadTime - startUploadTime });
        }
    }
    
    private void uploadDocFiles(final Collection<String> qImages, final Collection<String> aImages) {
        final String imagesTempPath = String.format("%s/%s", this.tempFilePath, "images");
        final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        try {
            if (!this.isReIndexing) {
                final String key = String.format("%s/%s", this.uploadJob.getUuid(), this.questionDoc.getName());
                s3Client.putObject(this.cmsConfig.getBucket(), key, this.zipFile.getInputStream(this.questionDoc), new ObjectMetadata());
                JobFileProcessor.LOGGER.info("Uploaded Question %s %s", new Object[] { this.question.getDocId(), this.question.getDocFile() });
            }
            this.updateFile(this.question = this.question.uploaded());
        }
        catch (final IOException e) {
            this.updateFileForError(e, this.question);
        }
        try {
            if (!this.isReIndexing) {
                final String key = String.format("%s/%s", this.uploadJob.getUuid(), this.answerDoc.getName());
                s3Client.putObject(this.cmsConfig.getBucket(), key, this.zipFile.getInputStream(this.answerDoc), new ObjectMetadata());
                JobFileProcessor.LOGGER.info("Uploaded Answer %s %s", new Object[] { this.answer.getDocId(), this.answer.getDocFile() });
            }
            this.updateFile(this.answer = this.answer.uploaded());
        }
        catch (final IOException e) {
            this.updateFileForError(e, this.answer);
        }
    }
    
    private void addIndexBatch(final String docId, final ParsedDoc parsedQuestionDoc, final ParsedDoc parsedAnswerDoc) throws IOException, URISyntaxException {
        final String questionText = ParsedDoc.extractText(parsedQuestionDoc);
        final String answerText = ParsedDoc.extractText(parsedAnswerDoc);
        this.indexingBatch.add(new IndexCommand(docId, questionText, answerText, this.knowledgeDoc, this.question, this.answer, this.isUpdate));
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
            JobFileProcessor.LOGGER.info("Updating JobFile Error %s", new Object[] { jobFile.toStringPretty() });
            return UploadJobDB.updateJobFileError(handle, jobFile);
        });
    }
    
    private void updateFile(final JobFile jobFile) {
        Database.withinTx(this.cmsConfig.getDb(), handle -> {
            try {
                JobFileProcessor.LOGGER.info("Updating JobFile %s", new Object[] { this.answer.toStringPretty() });
                UploadJobDB.updateJobFileStatus(handle, jobFile);
                return Integer.valueOf(UploadJobDB.updateJobFileStatus(handle, jobFile));
            }
            catch (final Exception e) {
                JobFileProcessor.LOGGER.error((Throwable)e, "Error while updating file status", new Object[0]);
                throw new RuntimeException(e);
            }
        });
    }
    
    private boolean _upsertDoc(final Handle handle, final KnowledgeDoc doc) {
        try {
            JobFileProcessor.LOGGER.info("Upserting Doc  %s", new Object[] { doc.getDocId() });
            return DocDB.upsertDoc(handle, doc);
        }
        catch (final Exception e) {
            JobFileProcessor.LOGGER.error((Throwable)e, "Error while Upserting doc", new Object[0]);
            throw new RuntimeException(e);
        }
    }
    
    private int _updateFile(final Handle handle, final JobFile question, final JobFile answer) {
        try {
            JobFileProcessor.LOGGER.info("Updating question JobFile %s", new Object[] { question.toStringPretty() });
            JobFileProcessor.LOGGER.info("Updating answer JobFile %s", new Object[] { answer.toStringPretty() });
            UploadJobDB.updateJobFileStatus(handle, question);
            return UploadJobDB.updateJobFileStatus(handle, answer);
        }
        catch (final Exception e) {
            JobFileProcessor.LOGGER.error((Throwable)e, "Error while updating file status", new Object[0]);
            throw new RuntimeException(e);
        }
    }
    
    private void updateFile(final JobFile question, final JobFile answer) {
        Database.withinTx(this.cmsConfig.getDb(), handle -> this._updateFile(handle, question, answer));
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)RemoteFileProcessor.class);
    }
}
