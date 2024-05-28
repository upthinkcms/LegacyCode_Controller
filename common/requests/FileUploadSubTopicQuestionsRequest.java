// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class FileUploadSubTopicQuestionsRequest extends GsonDto
{
    private final String uploadId;
    private final int subjectId;
    private final int topicId;
    private final int subTopicId;
    
    public FileUploadSubTopicQuestionsRequest(final String uploadId, final int subjectId, final int topicId, final int subTopicId) {
        this.uploadId = uploadId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
    }
    
    public String getUploadId() {
        return this.uploadId;
    }
    
    public int getSubjectId() {
        return this.subjectId;
    }
    
    public int getTopicId() {
        return this.topicId;
    }
    
    public int getSubTopicId() {
        return this.subTopicId;
    }
}
