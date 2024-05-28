// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class DeleteFileUploadRequest extends GsonDto
{
    private final String uploadId;
    private final String tenantKey;
    
    public DeleteFileUploadRequest(final String uploadId, final String tenantKey) {
        this.uploadId = uploadId;
        this.tenantKey = tenantKey;
    }
    
    public String getUploadId() {
        return this.uploadId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
