// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.auth.domain.TokenValidation;
import com.yojito.minima.gson.GsonObject;
import com.yojito.minima.auth.domain.AuthenticatedRequest;

public class PresignedURLRequest extends AuthenticatedRequest
{
    private final String jobId;
    private final String fileName;
    private final GsonObject os;
    
    public PresignedURLRequest(final TokenValidation id, final String jobId, final String fileName, final GsonObject os) {
        super(id);
        this.jobId = jobId;
        this.fileName = fileName;
        this.os = os;
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public GsonObject getOs() {
        return this.os;
    }
}
