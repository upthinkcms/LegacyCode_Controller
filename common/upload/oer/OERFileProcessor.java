// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload.oer;

import com.upthinkexperts.common.db.UploadJobDB;
import org.jdbi.v3.core.Handle;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.JsonPath;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.Configuration;
import java.util.List;
import com.upthinkexperts.common.domain.oer.OERDocument;
import com.amazonaws.services.s3.AmazonS3;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.domain.oer.OERSegment;
import com.upthinkexperts.common.parsing.OERDocConverter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import com.upthinkexperts.common.parsing.OERParser;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.yojito.minima.logging.MinimaLogger;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.parsing.ParsedDoc;
import com.upthinkexperts.common.domain.UploadJob;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import com.upthinkexperts.common.domain.JobFile;

public class OERFileProcessor implements Runnable
{
    private JobFile oerJobFile;
    private ZipEntry questionDocZipEntry;
    private ZipFile zipFile;
    private final UploadJob uploadJob;
    private ParsedDoc oerDoc;
    private final CmsCommonConfig cmsConfig;
    private String tempFilePath;
    private final OERBatch oerBatch;
    private static final MinimaLogger LOGGER;
    
    public OERFileProcessor(final JobFile oerJobFile, final UploadJob uploadJob, final CmsCommonConfig context, final OERBatch oerBatch, final ZipEntry questionDoc) {
        this.oerJobFile = oerJobFile;
        this.uploadJob = uploadJob;
        this.cmsConfig = context;
        this.oerBatch = oerBatch;
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
            OERFileProcessor.LOGGER.info("Processing JobFile  = %s", new Object[] { this.oerJobFile.getDocId() });
            docName = this.oerJobFile.getDocId();
            docId = this.oerJobFile.getDocId();
            final OERParser oerParser = new OERParser();
            parseStartTime = System.currentTimeMillis();
            try {
                this.oerDoc = OERParser.parse(docName, ParsingSpecs.OPENSTAX, this.zipFile.getInputStream(this.questionDocZipEntry), equationTempPath, imagesTempPath);
                this.oerJobFile = this.oerJobFile.parsed();
                OERFileProcessor.LOGGER.info("Parsed Doc %s %s", new Object[] { docName, this.oerJobFile.getDocFile() });
                Files.writeString(Paths.get(this.tempFilePath + "/parsedDoc.txt", new String[0]), this.oerDoc.toString(), new OpenOption[0]);
            }
            catch (final Exception e) {
                OERFileProcessor.LOGGER.warn((Throwable)e, "File %s Doc Id %s : Question Doc Parsing Error", new Object[] { this.oerJobFile.getDocFile(), this.oerJobFile.getDocId() });
                this.oerBatch.registerParsingFailure(docName, docId);
                this.updateFileForError(e, this.oerJobFile, JobFile.JobFileErrorCodes.QUESTION_FILE_PARSE_ERROR);
                return;
            }
            parseTime = System.currentTimeMillis();
            final OERDocConverter converter = new OERDocConverter();
            final OERDocument oerDocument = converter.convert(docId, this.oerDoc, this.uploadJob.getTenantKey());
            docId = oerDocument.getDocId();
            OERFileProcessor.LOGGER.debug("Retrieved real DocID = %s", new Object[] { docId });
            this.oerBatch.registerFileDocId(this.questionDocZipEntry.getName(), docId);
            final String finalizedDocId = docId;
            Database.doWithinTx(this.cmsConfig.getDb(), handle -> {
                this.upsertRootDoc(handle, oerDocument);
                final List<OERSegment> list = OERSegments.createAndReturnSegments(oerDocument, this.uploadJob.getTenantKey(), handle, this.uploadJob.getUuid());
                final String jobId = this.uploadJob.getUuid();
                final String key = String.format("%s/%s-segments.json", jobId, finalizedDocId);
                final String oerDocumentJson = oerDocument.toJson();
                s3Client.putObject(this.cmsConfig.getBucket(), key, oerDocumentJson);
                OERFileProcessor.LOGGER.info("Uploaded root docuemtn segment JSON at Path %s", new Object[] { key });
                list.stream().filter(s -> s.getType().equals(OERSegment.OERSegmentType.segment) && !s.getSegment().equals("document")).forEach(segment -> {
                    final String segmentKey = String.format("%s/%s-segment-%s.json", jobId, finalizedDocId, segment.getId());
                    final String segmentJson = this.getSegmentJson(oerDocumentJson, segment);
                    s3Client.putObject(this.cmsConfig.getBucket(), segmentKey, segmentJson);
                    OERFileProcessor.LOGGER.info("Uploaded segment JSON for segment %s - at Path %s", new Object[] { segment.toString(), segmentKey });
                });
                return;
            });
            startUploadTime = System.currentTimeMillis();
            this.uploadDocFiles(oerDocument);
            uploadTime = System.currentTimeMillis();
        }
        catch (final Exception e2) {
            OERFileProcessor.LOGGER.error((Throwable)e2, "Error while processing docFile for docId=%s", new Object[] { docId });
            this.updateFileForError(e2, this.oerJobFile);
        }
        finally {
            endTime = System.currentTimeMillis();
            OERFileProcessor.LOGGER.info("Total Time for %s = %d, withoutUpload = %d, ParsingTime= %d, ConvertTime=%d, start=%d, parse=%d, uploadTime=%d", new Object[] { docId, endTime - startTime, endTime - startUploadTime, parseTime - parseStartTime, parseTime - startUploadTime, startTime, parseTime, uploadTime - startUploadTime });
        }
    }
    
    private String getSegmentJson(final String document, final OERSegment segment) {
        final Configuration GSON_JSON_NODE_CONFIGURATION = Configuration.builder().jsonProvider((JsonProvider)new GsonJsonProvider()).options(new Option[] { Option.SUPPRESS_EXCEPTIONS }).build();
        final Configuration conf = Configuration.builder().jsonProvider((JsonProvider)new GsonJsonProvider()).options(new Option[] { Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS }).build();
        final JsonArray objArrJ = (JsonArray)JsonPath.using(conf).parse(document).read(segment.getPath(), new Predicate[0]);
        return objArrJ.get(0).getAsJsonObject().toString();
    }
    
    private void upsertRootDoc(final Handle handle, final OERDocument oerDocument) {
    }
    
    private void uploadDocFiles(final OERDocument oerDocument) {
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
            OERFileProcessor.LOGGER.info("Updating JobFile Error %s", new Object[] { jobFile.toStringPretty() });
            return UploadJobDB.updateJobFileError(handle, jobFile);
        });
    }
    
    private int _updateFile(final Handle handle, final JobFile qbank) {
        try {
            OERFileProcessor.LOGGER.info("Updating JobFile %s", new Object[] { qbank.toStringPretty() });
            UploadJobDB.updateJobFileStatus(handle, qbank);
            return UploadJobDB.updateJobFileStatus(handle, qbank);
        }
        catch (final Exception e) {
            OERFileProcessor.LOGGER.error((Throwable)e, "Error while updating file status", new Object[0]);
            throw new RuntimeException(e);
        }
    }
    
    public void setZipFile(final ZipFile zipFile) {
        this.zipFile = zipFile;
    }
    
    public void setTempFilePath(final String absolutePath) {
        this.tempFilePath = absolutePath;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERFileProcessor.class);
    }
}
