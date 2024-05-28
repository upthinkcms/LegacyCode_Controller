// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.controllers;

import com.upthinkexperts.common.domain.QBankDBQuestion;
import com.upthinkexperts.common.responses.FileUploadSubTopicQuestionsResponse;
import com.upthinkexperts.common.requests.FileUploadSubTopicQuestionsRequest;
import com.upthinkexperts.common.domain.QBankSubTopic;
import com.upthinkexperts.common.responses.FileUploadSubTopicResponse;
import com.upthinkexperts.common.requests.FileUploadSubTopicRequest;
import com.upthinkexperts.common.domain.QBankTopic;
import com.upthinkexperts.common.responses.FileUploadTopicsResponse;
import com.upthinkexperts.common.requests.FileUploadTopicsRequest;
import com.upthinkexperts.common.domain.DBSubject;
import com.upthinkexperts.common.responses.FileUploadSubjectsResponse;
import com.upthinkexperts.common.requests.FileUploadSubjectsRequest;
import com.upthinkexperts.common.db.QBankDB;
import com.upthinkexperts.common.requests.DeleteFileUploadRequest;
import java.util.concurrent.Executor;
import com.upthinkexperts.common.upload.oer.OERProcessing;
import java.util.concurrent.CompletableFuture;
import com.upthinkexperts.common.upload.UploadJobThread;
import com.yojito.minima.responses.BaseResponse;
import com.upthinkexperts.common.upload.QBankProcessing;
import com.upthinkexperts.common.responses.IndexingProgressResponse;
import com.upthinkexperts.common.responses.UploadJobDetails;
import com.upthinkexperts.common.requests.JobRequest;
import java.util.Optional;
import com.upthinkexperts.common.requests.UpdateStatusRequest;
import java.net.URL;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Date;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.upthinkexperts.common.responses.PresignedURLResponse;
import com.upthinkexperts.common.requests.PresignedURLRequest;
import java.util.Map;
import java.util.Collection;
import com.upthinkexperts.common.util.ListRequest;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;
import java.util.List;
import com.upthinkexperts.common.domain.JobFile;
import java.util.ArrayList;
import com.upthinkexperts.common.responses.ListUploadResponse;
import com.yojito.minima.api.API;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.UploadJobDB;
import java.util.UUID;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.UploadJob;
import org.jdbi.v3.core.Handle;
import com.upthinkexperts.common.requests.NewUploadRequest;
import com.yojito.minima.netty.HttpCall;
import com.yojito.minima.api.Context;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.api.ApiController;

public class UploadController extends ApiController
{
    private static final MinimaLogger LOGGER;
    
    public UploadController(final Context context) {
        super(context);
    }
    
    @API(path = "/cms/upload/new", corsEnabled = true, dbTx = true, authenticated = true, authRole = "SME_UPLOAD")
    public UploadJob newUpload(final HttpCall call, final NewUploadRequest request, final Handle handle) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (UploadJob)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final UploadJob uploadJob = new UploadJob(UUID.randomUUID().toString(), request.getFileName(), UploadJob.UploadJobStatus.EMPTY, request.getDocFormat(), request.getSubject(), request.getCategory(), request.getSubcategory(), request.getDifficulty(), null, request.getParsingSpec(), System.currentTimeMillis(), request.getTenantKey());
            UploadController.LOGGER.debug("UploadJob %s", new Object[] { uploadJob.toStringPretty() });
            UploadJobDB.insertUploadJob(cmsDbHandle, uploadJob);
            return uploadJob;
        });
    }
    
    @API(path = "/cms/upload/new_aidoc", corsEnabled = true, dbTx = true)
    public UploadJob newAiDocUpload(final HttpCall call, final NewUploadRequest request, final Handle handle) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (UploadJob)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final UploadJob uploadJob = new UploadJob(UUID.randomUUID().toString(), request.getFileName(), UploadJob.UploadJobStatus.EMPTY, request.getDocFormat(), request.getSubject(), request.getCategory(), request.getSubcategory(), request.getDifficulty(), null, request.getParsingSpec(), System.currentTimeMillis(), request.getTenantKey());
            UploadController.LOGGER.debug("AI Doc UploadJob: %s", new Object[] { uploadJob.toStringPretty() });
            UploadJobDB.insertUploadJob(cmsDbHandle, uploadJob);
            return uploadJob;
        });
    }
    
    @API(path = "/cms/upload/list", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public ListUploadResponse list(final HttpCall call, final NewUploadRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (ListUploadResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final List<UploadJob> uploadJobs = UploadJobDB.listUploadJob(cmsDbHandle, request.getTenantKey());
            final ArrayList<UploadJob> uploadJobsUpdated = new ArrayList<UploadJob>();
            uploadJobs.forEach(job -> {
                String status = job.getStatus().name();
                final boolean isJobProcessingFinished = job.getStatus().equals(UploadJob.UploadJobStatus.INDEXED_PROCESSED);
                final List<JobFile> files = UploadJobDB.listJobFiles(cmsDbHandle, job.getUuid(), job.getTenantKey());
                if (files.size() == 0) {
                    status = UploadJob.UploadJobStatus.EMPTY.name();
                }
                else if (isJobProcessingFinished) {
                    files.iterator();
                    final Iterator iterator;
                    while (iterator.hasNext()) {
                        final JobFile jobFile = iterator.next();
                        final boolean isFileProcessedCorrectly = jobFile.isParsed() || jobFile.isUploaded() || jobFile.isIndexed() || jobFile.getErrorCode() == null;
                        if (!isFileProcessedCorrectly) {
                            status = UploadJob.UploadJobStatus.INDEXED_PROCESSED_WITH_ERROR.name();
                            break;
                        }
                    }
                }
                uploadJobsUpdated.add(new UploadJob(job.getUuid(), job.getFileName(), status, job.getInfo().toString(), job.getDocFormat(), job.getUrl(), job.getParsingSpec(), job.getTimestamp(), job.getTenantKey(), files.size()));
                return;
            });
            return new ListUploadResponse(uploadJobsUpdated);
        });
    }
    
    @API(path = "/cms/listFiles", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public ListUploadResponse getListQBankFiles(final HttpCall call, final NewUploadRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        final AtomicInteger total = new AtomicInteger();
        final List<UploadJob> fileList = (List<UploadJob>)Database.withinTx(config.getDb(), cmsDBHandle -> {
            final List<UploadJob> uploadJobs = new ArrayList<UploadJob>();
            final HashMap<String, UploadJob.UploadJobStatus> uploadIdStatusMap = new HashMap<String, UploadJob.UploadJobStatus>();
            final ListRequest listRequest = new ListRequest(request.getPage(), request.getLimit(), request.getSortField(), request.getSortDirection());
            final List<UploadJob> uploadJobsTemp = UploadJobDB.listUploadJobByTenantKey(cmsDBHandle, request.getTenantKey(), listRequest.getPage(), listRequest.getLimit(), listRequest.getSortField(), listRequest.getSortDirection());
            if (uploadJobsTemp != null && !uploadJobsTemp.isEmpty()) {
                uploadJobsTemp.iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final UploadJob job = iterator.next();
                    uploadIdStatusMap.put(job.getUuid(), job.getStatus());
                }
                final List<String> uploadIds = new ArrayList<String>(uploadIdStatusMap.keySet());
                final List<JobFile> files = UploadJobDB.listJobFilesByUuid(cmsDBHandle, uploadIds, request.getTenantKey());
                final HashMap<String, List<JobFile>> filesMap = new HashMap<String, List<JobFile>>();
                List<JobFile> temps = new ArrayList<JobFile>();
                files.iterator();
                final Iterator iterator2;
                while (iterator2.hasNext()) {
                    final JobFile file = iterator2.next();
                    if (file != null) {
                        if (filesMap.containsKey(file.getJobId())) {
                            temps.addAll(filesMap.get(file.getJobId()));
                            temps.add(file);
                            filesMap.replace(file.getJobId(), temps);
                            temps = new ArrayList<JobFile>();
                        }
                        else {
                            filesMap.put(file.getJobId(), List.of(file));
                        }
                    }
                }
                uploadIdStatusMap.entrySet().iterator();
                final Iterator iterator3;
                while (iterator3.hasNext()) {
                    final Map.Entry<String, UploadJob.UploadJobStatus> map = iterator3.next();
                    UploadJob.UploadJobStatus status = map.getValue();
                    final List<JobFile> filesTemp = filesMap.get(map.getKey());
                    if (filesTemp == null || filesTemp.isEmpty()) {
                        status = UploadJob.UploadJobStatus.EMPTY;
                    }
                    else if (status == UploadJob.UploadJobStatus.INDEXED_PROCESSED) {
                        filesTemp.iterator();
                        final Iterator iterator4;
                        while (iterator4.hasNext()) {
                            final JobFile jobFile = iterator4.next();
                            final boolean isFileProcessedCorrectly = jobFile.isParsed() || jobFile.isUploaded() || jobFile.isIndexed() || jobFile.getError() == null;
                            if (!isFileProcessedCorrectly) {
                                status = UploadJob.UploadJobStatus.INDEXED_PROCESSED_WITH_ERROR;
                                break;
                            }
                        }
                    }
                    uploadIdStatusMap.replace(map.getKey(), status);
                }
                int fileSize = 0;
                uploadJobsTemp.iterator();
                final Iterator iterator5;
                while (iterator5.hasNext()) {
                    final UploadJob job2 = iterator5.next();
                    if (filesMap.get(job2.getUuid()) != null) {
                        fileSize = filesMap.get(job2.getUuid()).size();
                    }
                    uploadJobs.add(new UploadJob(job2.getUuid(), job2.getFileName(), uploadIdStatusMap.get(job2.getUuid()).name(), job2.getInfo().toString(), job2.getDocFormat(), job2.getUrl(), job2.getParsingSpec(), job2.getTimestamp(), job2.getTenantKey(), fileSize));
                }
            }
            total.set(UploadJobDB.totalListUploadJob(cmsDBHandle, request.getTenantKey()));
            return uploadJobs;
        });
        return new ListUploadResponse(fileList, total.get());
    }
    
    @API(path = "/cms/upload/presignUrl", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public PresignedURLResponse presignUrl(final HttpCall call, final PresignedURLRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (PresignedURLResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final String jobBucketKey = String.format("%s/%s", request.getJobId(), request.getFileName());
            String zipContentType = "application/x-zip-compressed";
            if (request.getOs() != null) {
                final String name = request.getOs().getString("name");
                if (name.contains("Windows")) {
                    zipContentType = "application/x-zip-compressed";
                }
                UploadController.LOGGER.info("Generating presign-url for OS = {%s} contentType={%s}", new Object[] { name, zipContentType });
            }
            final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            final Date expiration = Date.from(LocalDateTime.now().plus(10L, (TemporalUnit)ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant());
            UploadController.LOGGER.info("generatePresignedUrl PUT method contentType = [%s]", new Object[] { zipContentType });
            final URL url = s3Client.generatePresignedUrl(new GeneratePresignedUrlRequest(config.getBucket(), jobBucketKey).withExpiration(expiration).withMethod(HttpMethod.PUT).withContentType(zipContentType));
            UploadJobDB.updateUploadJobStatusAdnUrl(cmsDbHandle, request.getJobId(), UploadJob.UploadJobStatus.SIGNED, url.toString());
            return new PresignedURLResponse(url.toString(), request.getJobId());
        });
    }
    
    @API(path = "/cms/upload/status", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public UploadJob updateStatus(final HttpCall call, final UpdateStatusRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (UploadJob)Database.withinTx(config.getDb(), cmsDbHandle -> {
            UploadJobDB.updateUploadJobStatus(cmsDbHandle, request.getJobId(), request.getStatus());
            final Optional<UploadJob> uploadJob = UploadJobDB.loadUploadJob(cmsDbHandle, request.getJobId());
            return uploadJob.get();
        });
    }
    
    @API(path = "/cms/upload/details", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public UploadJobDetails jobDetails(final HttpCall call, final JobRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (UploadJobDetails)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final Optional<UploadJob> uploadJob = UploadJobDB.loadUploadJob(cmsDbHandle, request.getJobId());
            final List<JobFile> files = UploadJobDB.listJobFiles(cmsDbHandle, request.getJobId(), request.getTenantKey());
            return new UploadJobDetails(files, uploadJob.get());
        });
    }
    
    @API(path = "/cms/upload/progress", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public IndexingProgressResponse jobIndexingProgress(final HttpCall call, final JobRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (IndexingProgressResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final Optional<UploadJob> uploadJob = UploadJobDB.loadUploadJob(cmsDbHandle, request.getJobId());
            switch (uploadJob.get().getDocFormat()) {
                case CMS:
                case OER: {
                    final List<JobFile> files = UploadJobDB.listJobFiles(cmsDbHandle, request.getJobId(), request.getTenantKey());
                    final int totalSteps = files.size() * 3;
                    final AtomicInteger atomicInteger = new AtomicInteger();
                    files.forEach(jobFile -> {
                        if (jobFile.isError()) {
                            atomicInteger.addAndGet(3);
                        }
                        else {
                            if (jobFile.isIndexed()) {
                                atomicInteger.incrementAndGet();
                            }
                            if (jobFile.isParsed()) {
                                atomicInteger.incrementAndGet();
                            }
                            if (jobFile.isUploaded()) {
                                atomicInteger.incrementAndGet();
                            }
                        }
                        return;
                    });
                    final int stepsCompleted = atomicInteger.get();
                    switch (uploadJob.get().getStatus()) {
                        case EMPTY: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.EMPTY, files, 100, 1);
                        }
                        case ERROR: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.ERROR, files, 100, 100);
                        }
                        case SIGNED: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.SIGNED, files, 100, 1);
                        }
                        case INDEXED_PROCESSED: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.INDEXED_PROCESSED, files, totalSteps, stepsCompleted);
                        }
                        case INDEXED: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.INDEXED, files, totalSteps, stepsCompleted);
                        }
                        case INDEXING: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.INDEXING, files, totalSteps, stepsCompleted);
                        }
                        case UPLOADED: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.UPLOADED, files, 100, 1);
                        }
                        case UPLOADING: {
                            return new IndexingProgressResponse(UploadJob.UploadJobStatus.UPLOADING, files, 100, 1);
                        }
                        default: {
                            throw new RuntimeException("Unknown state");
                        }
                    }
                    break;
                }
                case QBANK: {
                    return QBankProcessing.checkProgress(request, cmsDbHandle, uploadJob);
                }
                default: {
                    throw new RuntimeException("Unknown DocFormat - " + uploadJob.get().getDocFormat());
                }
            }
        });
    }
    
    @API(path = "/cms/upload/process", corsEnabled = true, authenticated = true, authRole = "SME_UPLOAD")
    public BaseResponse process(final HttpCall call, final JobRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (BaseResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final Optional<UploadJob> jobOptional = UploadJobDB.loadUploadJob(cmsDbHandle, request.getJobId());
            if (jobOptional.isPresent()) {
                switch (jobOptional.get().getDocFormat()) {
                    case CMS: {
                        final Runnable processorRunnable = new UploadJobThread(jobOptional.get(), config);
                        final Executor uploadJobPool = config.getWorkerPool();
                        CompletableFuture.runAsync(processorRunnable, uploadJobPool);
                        return new BaseResponse(true, (String)null);
                    }
                    case QBANK: {
                        return QBankProcessing.process(config, jobOptional.get(), cmsDbHandle);
                    }
                    case OER: {
                        return OERProcessing.process(config, jobOptional.get(), cmsDbHandle);
                    }
                    default: {
                        throw new RuntimeException("Unknown DocFormat - " + jobOptional.get().getDocFormat());
                    }
                }
            }
            else {
                return new BaseResponse(false, "Cant find jobId=" + request.getJobId());
            }
        });
    }
    
    @API(path = "/cms/upload/delete", corsEnabled = true, authenticated = false)
    public BaseResponse deleteUploadedFile(final HttpCall call, final DeleteFileUploadRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (BaseResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            UploadJobDB.softDeleteFileUpload(cmsDbHandle, request.getUploadId(), request.getTenantKey());
            QBankDB.softDeleteQuestionsInFileUpload(cmsDbHandle, request.getUploadId());
            return new BaseResponse(true, (String)null);
        });
    }
    
    @API(path = "/cms/upload/getSubjects", corsEnabled = true, authenticated = false)
    public FileUploadSubjectsResponse getSubjectsForFileUpload(final HttpCall call, final FileUploadSubjectsRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (FileUploadSubjectsResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final List<DBSubject> subjectList = QBankDB.getSubjectsForFileUpload(cmsDbHandle, request.getId());
            return new FileUploadSubjectsResponse(subjectList);
        });
    }
    
    @API(path = "/cms/upload/getTopics", corsEnabled = true, authenticated = false)
    public FileUploadTopicsResponse getTopicsForFileUpload(final HttpCall call, final FileUploadTopicsRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (FileUploadTopicsResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final List<QBankTopic> topicList = QBankDB.getTopicsForFileUploadFromSubjectId(cmsDbHandle, request.getUploadId(), request.getSubjectId());
            return new FileUploadTopicsResponse(topicList);
        });
    }
    
    @API(path = "/cms/upload/getSubTopics", corsEnabled = true, authenticated = false)
    public FileUploadSubTopicResponse getSubTopicsForFileUpload(final HttpCall call, final FileUploadSubTopicRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (FileUploadSubTopicResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final List<QBankSubTopic> topicList = QBankDB.getSubTopicsForFileUploadFromTopicId(cmsDbHandle, request.getUploadId(), request.getTopicId());
            return new FileUploadSubTopicResponse(topicList);
        });
    }
    
    @API(path = "/cms/upload/getQuestionsForSubTopic", corsEnabled = true, authenticated = false)
    public FileUploadSubTopicQuestionsResponse getQuestionsForSubTopicInFile(final HttpCall call, final FileUploadSubTopicQuestionsRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (FileUploadSubTopicQuestionsResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            final List<QBankDBQuestion> questionList = QBankDB.getQuestionsForSubTopicFromFileUpload(cmsDbHandle, request.getUploadId(), request.getSubjectId(), request.getTopicId(), request.getSubTopicId());
            return new FileUploadSubTopicQuestionsResponse(questionList);
        });
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)UploadController.class);
    }
}
