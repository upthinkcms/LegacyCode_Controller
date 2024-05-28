// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public static class FixedQuestion extends GsonDto
{
    private final String docId;
    private final int qId;
    private final int subjectId;
    private final int topicId;
    private final int subTopicId;
    
    public FixedQuestion(final String docId, final int qId, final int subjectId, final int topicId, final int subTopicId) {
        this.docId = docId;
        this.qId = qId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public int getQId() {
        return this.qId;
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
