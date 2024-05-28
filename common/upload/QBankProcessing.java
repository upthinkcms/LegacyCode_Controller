// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;
import com.yojito.minima.responses.BaseResponse;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.JobFile;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.upthinkexperts.common.db.UploadJobDB;
import com.upthinkexperts.common.responses.IndexingProgressResponse;
import com.upthinkexperts.common.domain.UploadJob;
import java.util.Optional;
import org.jdbi.v3.core.Handle;
import com.upthinkexperts.common.requests.JobRequest;
import com.yojito.minima.logging.MinimaLogger;

public class QBankProcessing
{
    private static final MinimaLogger LOGGER;
    
    public static IndexingProgressResponse checkProgress(final JobRequest request, final Handle handle, final Optional<UploadJob> uploadJob) {
        final List<JobFile> files = UploadJobDB.listJobFiles(handle, request.getJobId(), uploadJob.get().getTenantKey());
        final int totalSteps = files.size();
        final AtomicInteger atomicInteger = new AtomicInteger();
        files.forEach(jobFile -> {
            if (jobFile.isError()) {
                atomicInteger.addAndGet(1);
            }
            else if (jobFile.isParsed()) {
                atomicInteger.incrementAndGet();
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
    }
    
    public static BaseResponse process(final CmsCommonConfig context, final UploadJob job, final Handle handle) {
        QBankProcessing.LOGGER.info("Scheduling processing of Job = %s", new Object[] { job.toString() });
        final Runnable processorRunnable = new QBankProcessingThread(job, context);
        final Executor uploadJobPool = context.getWorkerPool();
        final CompletableFuture future = CompletableFuture.runAsync(processorRunnable, uploadJobPool);
        QBankProcessing.LOGGER.info("Scheduled processing of Job = %s -> %s", new Object[] { job.toString(), future });
        return new BaseResponse(true, (String)null);
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankProcessing.class);
    }
}
