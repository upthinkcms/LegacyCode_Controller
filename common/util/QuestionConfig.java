// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

public class QuestionConfig
{
    private final Integer qId;
    private final Integer subjectId;
    private final Integer topicId;
    private final Integer subTopicId;
    private final String tenantKey;
    private final String docId;
    
    public QuestionConfig(final Integer qId, final Integer subjectId, final Integer topicId, final Integer subTopicId, final String tenantKey, final String docId) {
        this.qId = qId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.tenantKey = tenantKey;
        this.docId = docId;
    }
    
    public Integer getQId() {
        return this.qId;
    }
    
    public Integer getSubjectId() {
        return this.subjectId;
    }
    
    public Integer getTopicId() {
        return this.topicId;
    }
    
    public Integer getSubTopicId() {
        return this.subTopicId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
    
    public String getDocId() {
        return this.docId;
    }
}
