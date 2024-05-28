// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class FileUploadSubjectsRequest extends GsonDto
{
    private final String uploadId;
    
    public FileUploadSubjectsRequest(final String uploadId) {
        this.uploadId = uploadId;
    }
    
    public String getId() {
        return this.uploadId;
    }
}
