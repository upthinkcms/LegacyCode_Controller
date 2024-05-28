// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class FileUploadSubTopicRequest extends GsonDto
{
    private final int topicId;
    private final String uploadId;
    
    public FileUploadSubTopicRequest(final int topicId, final String uploadId) {
        this.topicId = topicId;
        this.uploadId = uploadId;
    }
    
    public int getTopicId() {
        return this.topicId;
    }
    
    public String getUploadId() {
        return this.uploadId;
    }
}
