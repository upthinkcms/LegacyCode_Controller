// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload.oer;

import org.jdbi.v3.core.Handle;
import java.util.List;
import java.util.Map;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.UploadJobDB;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.logging.MinimaLogger;

public class OERProcessingThread implements Runnable
{
    private static final MinimaLogger LOGGER;
    private final UploadJob job;
    private final CmsCommonConfig context;
    
    public OERProcessingThread(final UploadJob job, final CmsCommonConfig context) {
        this.job = job;
        this.context = context;
    }
    
    @Override
    public void run() {
        final long l1 = System.currentTimeMillis();
        try {
            final OERProcessor processor = new OERProcessor(this.job, this.context);
            processor.downloadAndExtract();
            OERProcessingThread.LOGGER.info("Downloaded and extracted Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.createFileEntries();
            OERProcessingThread.LOGGER.info("Created File Entries for Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.processDocs();
            processor.awaitDocProcessing();
            final Map<String, List<String>> failedEquations = processor.processEquations();
            OERProcessingThread.LOGGER.info("Processed Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.createImagesForFailedEquations(failedEquations);
            OERProcessingThread.LOGGER.info("Processed Non-MathType Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.processImages();
            OERProcessingThread.LOGGER.info("Processed Images from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.cleanup();
            processor.markComplete();
            OERProcessingThread.LOGGER.info("Upload Job %s(%s) is completed Time taken = %dms", new Object[] { this.job.getFileName(), this.job.getUuid(), System.currentTimeMillis() - l1 });
        }
        catch (final Exception e) {
            OERProcessingThread.LOGGER.error((Throwable)e, "Error processing remote file", new Object[0]);
            Database.doWithinTx(this.context.getDb(), handle1 -> UploadJobDB.updateUploadJobStatus(handle1, this.job.getUuid(), UploadJob.UploadJobStatus.ERROR));
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERProcessingThread.class);
    }
}
