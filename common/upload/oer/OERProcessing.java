// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload.oer;

import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;
import com.yojito.minima.responses.BaseResponse;
import org.jdbi.v3.core.Handle;
import com.upthinkexperts.common.domain.UploadJob;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.yojito.minima.logging.MinimaLogger;

public class OERProcessing
{
    private static final MinimaLogger LOGGER;
    
    public static BaseResponse process(final CmsCommonConfig context, final UploadJob job, final Handle cmsDbHandle) {
        OERProcessing.LOGGER.info("Scheduling processing of Job = %s", new Object[] { job.toString() });
        final Runnable processorRunnable = new OERProcessingThread(job, context);
        final Executor uploadJobPool = context.getWorkerPool();
        final CompletableFuture future = CompletableFuture.runAsync(processorRunnable, uploadJobPool);
        OERProcessing.LOGGER.info("Scheduled processing of Job = %s -> %s", new Object[] { job.toString(), future });
        return new BaseResponse(true, (String)null);
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERProcessing.class);
    }
}
