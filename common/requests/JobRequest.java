// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.auth.domain.TokenValidation;
import com.yojito.minima.auth.domain.AuthenticatedRequest;

public class JobRequest extends AuthenticatedRequest
{
    private final String jobId;
    private final String tenantKey;
    
    public JobRequest(final TokenValidation id, final String jobId, final String tenantKey) {
        super(id);
        this.jobId = jobId;
        this.tenantKey = tenantKey;
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
