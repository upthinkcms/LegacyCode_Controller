// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import org.jdbi.v3.core.Handle;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.UploadJobDB;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.logging.MinimaLogger;

public class UploadJobThread implements Runnable
{
    private static final MinimaLogger LOGGER;
    private final UploadJob job;
    private final CmsCommonConfig cmsConfig;
    private final boolean reIndexing;
    
    public UploadJobThread(final UploadJob job, final CmsCommonConfig context) {
        this.job = job;
        this.cmsConfig = context;
        this.reIndexing = false;
    }
    
    public UploadJobThread(final UploadJob job, final CmsCommonConfig context, final boolean reIndexing) {
        this.job = job;
        this.cmsConfig = context;
        this.reIndexing = reIndexing;
    }
    
    @Override
    public void run() {
        final long l1 = System.currentTimeMillis();
        try {
            final RemoteFileProcessor processor = new RemoteFileProcessor(this.job, this.cmsConfig, ParsingSpecs.getParsingSpec(this.job.getParsingSpec()));
            processor.setReIndexing(this.reIndexing);
            processor.downloadAndExtract();
            UploadJobThread.LOGGER.info("Downloaded and extracted Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.createFileEntries();
            UploadJobThread.LOGGER.info("Created File Entries for Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.prepareForIndexing();
            UploadJobThread.LOGGER.info("Gathered Text from Document from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            processor.batchIndex();
            UploadJobThread.LOGGER.info("Indexed Documents from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
            if (processor.hasDocsForProcessing()) {
                final Map<String, Pair<List<String>, List<String>>> failedEquations = processor.processEquations();
                UploadJobThread.LOGGER.info("Processed Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
                processor.createImagesForFailedEquations(failedEquations);
                UploadJobThread.LOGGER.info("Processed Non-MathType Equations from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
                processor.processImages();
                UploadJobThread.LOGGER.info("Processed Images from Remote File %s of job %s", new Object[] { this.job.getFileName(), this.job.getUuid() });
                processor.cleanup();
            }
            else {
                UploadJobThread.LOGGER.warn("No Docs available for Image and Equations Processing*******", new Object[0]);
            }
            processor.markComplete();
            UploadJobThread.LOGGER.info("Upload Job %s(%s) is completed Time taken = %dms", new Object[] { this.job.getFileName(), this.job.getUuid(), System.currentTimeMillis() - l1 });
        }
        catch (final Exception e) {
            UploadJobThread.LOGGER.error((Throwable)e, "Error processing remote file", new Object[0]);
            Database.doWithinTx(this.cmsConfig.getDb(), handle1 -> UploadJobDB.updateUploadJobStatus(handle1, this.job.getUuid(), UploadJob.UploadJobStatus.ERROR));
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)UploadJobThread.class);
    }
}
