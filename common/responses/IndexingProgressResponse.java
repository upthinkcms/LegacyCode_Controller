// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.yojito.minima.gson.GsonObject;
import com.upthinkexperts.common.domain.JobFile;
import java.util.List;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.gson.GsonDto;

public class IndexingProgressResponse extends GsonDto
{
    private final UploadJob.UploadJobStatus status;
    private final List<JobFile> files;
    private final int totalSteps;
    private final int stepsCompleted;
    private final GsonObject errorInfo;
    
    public IndexingProgressResponse(final UploadJob.UploadJobStatus status, final List<JobFile> files, final int totalSteps, final int stepsCompleted) {
        this.status = status;
        this.files = files;
        this.totalSteps = totalSteps;
        this.stepsCompleted = stepsCompleted;
        this.errorInfo = null;
    }
    
    public IndexingProgressResponse(final UploadJob.UploadJobStatus status, final List<JobFile> files, final int totalSteps, final int stepsCompleted, final GsonObject errorInfo) {
        this.status = status;
        this.files = files;
        this.totalSteps = totalSteps;
        this.stepsCompleted = stepsCompleted;
        this.errorInfo = errorInfo;
    }
}
