// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import org.jdbi.v3.core.Handle;
import java.util.List;
import java.util.Map;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.UploadJobDB;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.logging.MinimaLogger;

public class QBankProcessingThread implements Runnable
{
    private static final MinimaLogger LOGGER;
    private final UploadJob job;
    private final CmsCommonConfig context;
    
    public QBankProcessingThread(final UploadJob job, final CmsCommonConfig context) {
        this.job = job;
        this.context = context;
    }
    
    @Override
    public void run() {
        final long l1 = System.currentTimeMillis();
        try {
            final QBankProcessor processor = new QBankProcessor(this.job, this.context);
            processor.downloadAndExtract();
            QBankProcessingThread.LOGGER.info("Downloaded and extracted Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.createFileEntries();
            QBankProcessingThread.LOGGER.info("Created File Entries for Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.processDocs();
            processor.awaitDocProcessing();
            final Map<String, List<String>> failedEquations = processor.processEquations();
            QBankProcessingThread.LOGGER.info("Processed Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.createImagesForFailedEquations(failedEquations);
            QBankProcessingThread.LOGGER.info("Processed Non-MathType Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.processImages();
            QBankProcessingThread.LOGGER.info("Processed Images from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.cleanup();
            processor.markComplete();
            QBankProcessingThread.LOGGER.info("Upload Job %s(%s) is completed Time taken = %dms", new Object[] { this.job.getFileName(), this.job.getUuid(), System.currentTimeMillis() - l1 });
        }
        catch (final Exception e) {
            QBankProcessingThread.LOGGER.error((Throwable)e, "Error processing remote file", new Object[0]);
            Database.doWithinTx(this.context.getDb(), handle1 -> UploadJobDB.updateUploadJobStatus(handle1, this.job.getUuid(), UploadJob.UploadJobStatus.ERROR));
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)UploadJobThread.class);
    }
}
