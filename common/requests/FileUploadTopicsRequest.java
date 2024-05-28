// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class FileUploadTopicsRequest extends GsonDto
{
    private final int subjectId;
    private final String uploadId;
    
    public FileUploadTopicsRequest(final int subjectId, final String uploadId) {
        this.subjectId = subjectId;
        this.uploadId = uploadId;
    }
    
    public int getSubjectId() {
        return this.subjectId;
    }
    
    public String getUploadId() {
        return this.uploadId;
    }
}
