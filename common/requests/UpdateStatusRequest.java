// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.auth.domain.TokenValidation;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.auth.domain.AuthenticatedRequest;

public class UpdateStatusRequest extends AuthenticatedRequest
{
    private final String jobId;
    private final UploadJob.UploadJobStatus status;
    
    public UpdateStatusRequest(final TokenValidation id, final String jobId, final UploadJob.UploadJobStatus status) {
        super(id);
        this.jobId = jobId;
        this.status = status;
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public UploadJob.UploadJobStatus getStatus() {
        return this.status;
    }
}
