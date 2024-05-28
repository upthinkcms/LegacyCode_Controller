// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import java.sql.SQLException;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.Query;
import java.util.Optional;
import org.jdbi.v3.core.statement.PreparedBatch;
import com.upthinkexperts.common.domain.JobFile;
import java.util.Collection;
import org.jdbi.v3.core.statement.Update;
import com.upthinkexperts.common.domain.UploadJob;
import org.jdbi.v3.core.Handle;

public class UploadJobDB
{
    private static final String FILE_UPLOADS_INSERT = "INSERT INTO FILE_UPLOADS(uploadId, fileName, status, info, parsingSpec, docFormat, tenantKey) VALUES(:uploadId, :fileName, :status, to_json(:info\\:\\:json), :parsingSpec, :docFormat, :tenantKey)";
    private static final String LOAD_FILE_UPLOADS = "select * from FILE_UPLOADS where uploadId = :uploadId";
    private static final String DELETE_ALL_FILE_UPLOADS = "delete from FILE_UPLOADS";
    private static final String LIST_FILE_UPLOADS = "select * from FILE_UPLOADS where docFormat in ('CMS', 'QBANK') and tenantKey = :tenantKey and deleted = false ORDER BY created_at DESC";
    private static final String LIST_FILE_UPLOADS_BY_TENANT_KEY = "select * from FILE_UPLOADS where docFormat in ('CMS', 'QBANK') and tenantKey = :tenantKey and deleted = false ";
    private static final String SOFT_DELETE_FILE_UPLOAD = "update file_uploads set deleted = true where tenantKey = :tenantKey and uploadid = :uploadId";
    private static final String UPDATE_FILE_UPLOADS_STATUS = "UPDATE FILE_UPLOADS set status =  :status where uploadId = :uploadId";
    private static final String UPDATE_FILE_UPLOADS_STATUS_AND_URL = "UPDATE FILE_UPLOADS set status = :status, url = :url where uploadId = :uploadId";
    private static final String JOB_FILE_INSERT = "INSERT INTO FILE_UPLOAD_DOC_FILES(docId, uploadId, docFile, isQuestion, parsed, indexed, uploaded, tenantKey) VALUES(:docId, :uploadId, :docFile, :isQuestion, :parsed, :indexed, :uploaded, :tenantKey)";
    private static final String LIST_JOB_FILE_INSERT = "select * from FILE_UPLOAD_DOC_FILES where uploadId = :uploadId and tenantKey = :tenantKey";
    private static final String UPDATE_JOB_FILE_STATUS = "UPDATE FILE_UPLOAD_DOC_FILES set parsed = :parsed, indexed=:indexed, uploaded= :uploaded where docId = :docId and uploadId = :uploadId and docFile = :docFile";
    private static final String UPDATE_JOB_FILE_STATUS_INDEXING = "UPDATE FILE_UPLOAD_DOC_FILES set indexed=:indexed where docId = :docId and uploadId = :uploadId and docFile = :docFile";
    private static final String UPDATE_JOB_FILE_ERROR = "UPDATE FILE_UPLOAD_DOC_FILES set error = :error, errorCode=:errorCode where docId = :docId and uploadId = :uploadId and docFile = :docFile";
    private static final String DELETE_ALL_FILE_UPLOAD_DOC_FILES = "delete from FILE_UPLOAD_DOC_FILES";
    private static final String DELETE_FILE_UPLOAD_DOC_FILE = "delete from FILE_UPLOAD_DOC_FILES where docId = :docId";
    private static final String GET_DOC_FILE = "select docFile, uploadId from FILE_UPLOAD_DOC_FILES where docId = :docId and isQuestion = :isQuestion";
    private static final String TOTAL_LIST_FILE_UPLOADS = "SELECT count(*) FROM FILE_UPLOADS WHERE docFormat in('CMS', 'QBANK')  AND tenantKey = :tenantKey AND deleted = false ";
    private static final String LIST_JOB_FILES_BY_UUID = "SELECT * FROM FILE_UPLOAD_DOC_FILES WHERE uploadId IN (<uuids>)  AND tenantKey = :tenantKey";
    
    public static int insertUploadJob(final Handle dbHandle, final UploadJob job) {
        return ((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("INSERT INTO FILE_UPLOADS(uploadId, fileName, status, info, parsingSpec, docFormat, tenantKey) VALUES(:uploadId, :fileName, :status, to_json(:info\\:\\:json), :parsingSpec, :docFormat, :tenantKey)").bind("uploadId", job.getUuid())).bind("fileName", job.getFileName())).bind("status", job.getStatus().name())).bind("info", job.getInfo().toJson())).bind("parsingSpec", job.getParsingSpec())).bind("docFormat", job.getDocFormat().name())).bind("tenantKey", job.getTenantKey())).execute();
    }
    
    public static int[] insertUploadJobFiles(final Handle dbHandle, final Collection<JobFile> jobFiles, final String tenantKey) {
        final PreparedBatch batch = dbHandle.prepareBatch("INSERT INTO FILE_UPLOAD_DOC_FILES(docId, uploadId, docFile, isQuestion, parsed, indexed, uploaded, tenantKey) VALUES(:docId, :uploadId, :docFile, :isQuestion, :parsed, :indexed, :uploaded, :tenantKey)");
        jobFiles.forEach(jobFile -> ((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)batch.bind("uploadId", jobFile.getJobId())).bind("tenantKey", tenantKey)).bind("docId", jobFile.getDocId())).bind("docFile", jobFile.getDocFile())).bind("isQuestion", jobFile.isQuestion())).bind("parsed", jobFile.isParsed())).bind("indexed", jobFile.isIndexed())).bind("uploaded", jobFile.isUploaded())).add());
        return batch.execute();
    }
    
    public static Optional<UploadJob> loadUploadJob(final Handle dbHandle, final String uuid) {
        return ((Query)dbHandle.createQuery("select * from FILE_UPLOADS where uploadId = :uploadId").bind("uploadId", uuid)).map((RowMapper)new RowMapper<UploadJob>() {
            public UploadJob map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String uploadId = rs.getString("uploadId");
                final String fileName = rs.getString("fileName");
                final String status = rs.getString("status");
                final String info = rs.getString("info");
                final String url = rs.getString("url");
                final String docFormatStr = rs.getString("docFormat");
                final String parsingSpec = rs.getString("parsingSpec");
                final String tenantKey = rs.getString("tenantKey");
                final long timestamp = rs.getTimestamp("created_at").getTime();
                final UploadJob uploadJob = new UploadJob(uploadId, fileName, status, info, ParsingSpec.DocFormat.valueOf(docFormatStr), url, parsingSpec, timestamp, tenantKey);
                return uploadJob;
            }
        }).findFirst();
    }
    
    public static Optional<Pair<String, String>> getDocFile(final Handle dbHandle, final String docId, final boolean isQuestion) {
        return ((Query)((Query)dbHandle.createQuery("select docFile, uploadId from FILE_UPLOAD_DOC_FILES where docId = :docId and isQuestion = :isQuestion").bind("docId", docId)).bind("isQuestion", isQuestion)).map((RowMapper)new RowMapper<Pair<String, String>>() {
            public Pair<String, String> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String docFile = rs.getString("docFile");
                final String uploadId = rs.getString("uploadId");
                return (Pair<String, String>)Pair.of((Object)uploadId, (Object)docFile);
            }
        }).findOne();
    }
    
    public static List<UploadJob> listUploadJob(final Handle dbHandle, final String tenantKey) {
        return ((Query)dbHandle.createQuery("select * from FILE_UPLOADS where docFormat in ('CMS', 'QBANK') and tenantKey = :tenantKey and deleted = false ORDER BY created_at DESC").bind("tenantKey", tenantKey)).map((RowMapper)new RowMapper<UploadJob>() {
            public UploadJob map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String uploadId = rs.getString("uploadId");
                final String fileName = rs.getString("fileName");
                final String status = rs.getString("status");
                final String info = rs.getString("info");
                final String url = rs.getString("url");
                final String parsingSpec = rs.getString("parsingSpec");
                final long timestamp = rs.getTimestamp("created_at").getTime();
                final String docFormatStr = rs.getString("docFormat");
                final String tenantKey = rs.getString("tenantKey");
                final UploadJob uploadJob = new UploadJob(uploadId, fileName, status, info, ParsingSpec.DocFormat.valueOf(docFormatStr), url, parsingSpec, timestamp, tenantKey);
                return uploadJob;
            }
        }).list();
    }
    
    public static List<UploadJob> listUploadJobByTenantKey(final Handle dbHandle, final String tenantKey, final int page, final int limit, final String sortField, final String sortDirection) {
        final String rawQuery = "select * from FILE_UPLOADS where docFormat in ('CMS', 'QBANK') and tenantKey = :tenantKey and deleted = false " + String.format(" ORDER BY %s %s ", sortField, sortDirection) + " LIMIT :limit OFFSET :page ";
        final Query query = (Query)((Query)((Query)dbHandle.createQuery(rawQuery).bind("tenantKey", tenantKey)).bind("limit", limit)).bind("page", page);
        return query.map((rs, ctx) -> {
            final String uploadId = rs.getString("uploadId");
            final String fileName = rs.getString("fileName");
            final String status = rs.getString("status");
            final String info = rs.getString("info");
            final String url = rs.getString("url");
            final String parsingSpec = rs.getString("parsingSpec");
            final long timestamp = rs.getTimestamp("created_at").getTime();
            final String docFormatStr = rs.getString("docFormat");
            final String tenantKey1 = rs.getString("tenantKey");
            return new UploadJob(uploadId, fileName, status, info, ParsingSpec.DocFormat.valueOf(docFormatStr), url, parsingSpec, timestamp, tenantKey1);
        }).list();
    }
    
    public static int totalListUploadJob(final Handle handle, final String tenantKey) {
        return (int)((Query)handle.createQuery("SELECT count(*) FROM FILE_UPLOADS WHERE docFormat in('CMS', 'QBANK')  AND tenantKey = :tenantKey AND deleted = false ").bind("tenantKey", tenantKey)).mapTo((Class)Integer.class).one();
    }
    
    public static List<JobFile> listJobFiles(final Handle dbHandle, final String uploadId, final String tenantKey) {
        return ((Query)((Query)dbHandle.createQuery("select * from FILE_UPLOAD_DOC_FILES where uploadId = :uploadId and tenantKey = :tenantKey").bind("uploadId", uploadId)).bind("tenantKey", tenantKey)).map((RowMapper)new RowMapper<JobFile>() {
            public JobFile map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String uploadId = rs.getString("uploadId");
                final String docId = rs.getString("docId");
                final String docFile = rs.getString("docFile");
                final boolean isQuestion = rs.getBoolean("isQuestion");
                final boolean parsed = rs.getBoolean("parsed");
                final boolean indexed = rs.getBoolean("indexed");
                final boolean uploaded = rs.getBoolean("uploaded");
                final String error = rs.getString("error");
                final String errorCodeString = rs.getString("errorCode");
                final String tenantKey = rs.getString("tenantKey");
                final JobFile.JobFileErrorCodes errorCode = (errorCodeString != null) ? JobFile.JobFileErrorCodes.valueOf(errorCodeString) : null;
                final JobFile jobFile = new JobFile(docId, uploadId, docFile, isQuestion, parsed, indexed, uploaded, error, errorCode, tenantKey);
                return jobFile;
            }
        }).list();
    }
    
    public static List<JobFile> listJobFilesByUuid(final Handle dbHandle, final List<String> uuids, final String tenantKey) {
        return ((Query)((Query)dbHandle.createQuery("SELECT * FROM FILE_UPLOAD_DOC_FILES WHERE uploadId IN (<uuids>)  AND tenantKey = :tenantKey").bindList("uuids", (Iterable)uuids)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String uploadId = rs.getString("uploadId");
            final String docId = rs.getString("docId");
            final String docFile = rs.getString("docFile");
            final boolean isQuestion = rs.getBoolean("isQuestion");
            final boolean parsed = rs.getBoolean("parsed");
            final boolean indexed = rs.getBoolean("indexed");
            final boolean uploaded = rs.getBoolean("uploaded");
            final String error = rs.getString("error");
            final String errorCodeString = rs.getString("errorCode");
            final String tenantKey1 = rs.getString("tenantKey");
            final JobFile.JobFileErrorCodes errorCode = (errorCodeString != null) ? JobFile.JobFileErrorCodes.valueOf(errorCodeString) : null;
            return new JobFile(docId, uploadId, docFile, isQuestion, parsed, indexed, uploaded, error, errorCode, tenantKey1);
        }).list();
    }
    
    public static int updateUploadJobStatus(final Handle dbHandle, final String uuid, final UploadJob.UploadJobStatus status) {
        return ((Update)((Update)dbHandle.createUpdate("UPDATE FILE_UPLOADS set status =  :status where uploadId = :uploadId").bind("uploadId", uuid)).bind("status", status.name())).execute();
    }
    
    public static int updateJobFileStatus(final Handle dbHandle, final JobFile jobFile) {
        final int updaetd = ((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("UPDATE FILE_UPLOAD_DOC_FILES set parsed = :parsed, indexed=:indexed, uploaded= :uploaded where docId = :docId and uploadId = :uploadId and docFile = :docFile").bind("uploadId", jobFile.getJobId())).bind("docFile", jobFile.getDocFile())).bind("docId", jobFile.getDocId())).bind("parsed", jobFile.isParsed())).bind("indexed", jobFile.isIndexed())).bind("uploaded", jobFile.isUploaded())).execute();
        System.out.printf("Updated >>> %s : %d\n", jobFile.toStringPretty(), updaetd);
        return updaetd;
    }
    
    public static int[] updateBatchJobFileIndexingStatus(final Handle dbHandle, final List<JobFile> jobFiles) {
        final PreparedBatch batch = dbHandle.prepareBatch("UPDATE FILE_UPLOAD_DOC_FILES set indexed=:indexed where docId = :docId and uploadId = :uploadId and docFile = :docFile");
        jobFiles.forEach(jobFile -> ((PreparedBatch)((PreparedBatch)((PreparedBatch)((PreparedBatch)batch.bind("uploadId", jobFile.getJobId())).bind("docFile", jobFile.getDocFile())).bind("docId", jobFile.getDocId())).bind("indexed", jobFile.isIndexed())).add());
        return batch.execute();
    }
    
    public static int updateUploadJobStatusAdnUrl(final Handle dbHandle, final String uuid, final UploadJob.UploadJobStatus status, final String url) {
        return ((Update)((Update)((Update)dbHandle.createUpdate("UPDATE FILE_UPLOADS set status = :status, url = :url where uploadId = :uploadId").bind("uploadId", uuid)).bind("status", status.name())).bind("url", url)).execute();
    }
    
    public static Object updateJobFileError(final Handle dbHandle, final JobFile jobFile) {
        return ((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("UPDATE FILE_UPLOAD_DOC_FILES set error = :error, errorCode=:errorCode where docId = :docId and uploadId = :uploadId and docFile = :docFile").bind("uploadId", jobFile.getJobId())).bind("docFile", jobFile.getDocFile())).bind("docId", jobFile.getDocId())).bind("error", jobFile.getError())).bind("errorCode", jobFile.getErrorCode().name())).execute();
    }
    
    public static void deleteUploads(final Handle dbHandle) {
        dbHandle.createUpdate("delete from FILE_UPLOAD_DOC_FILES").execute();
        dbHandle.createUpdate("delete from FILE_UPLOADS").execute();
    }
    
    public static void deleteUploadDocFile(final Handle dbHandle, final String docId) {
        ((Update)dbHandle.createUpdate("delete from FILE_UPLOAD_DOC_FILES where docId = :docId").bind("docId", docId)).execute();
    }
    
    public static void softDeleteFileUpload(final Handle dbHandle, final String uploadId, final String tenantKey) {
        ((Update)((Update)dbHandle.createUpdate("update file_uploads set deleted = true where tenantKey = :tenantKey and uploadid = :uploadId").bind("tenantKey", tenantKey)).bind("uploadId", uploadId)).execute();
    }
}
