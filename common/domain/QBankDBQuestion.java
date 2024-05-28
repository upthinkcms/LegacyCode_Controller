// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class QBankDBQuestion extends GsonDto
{
    private final int id;
    private final String docId;
    private final int courseId;
    private final String specifiedQid;
    private final int subjectId;
    private final int topicId;
    private final int subTopicId;
    private final int level;
    private final String type;
    private final String answer;
    private final String qDocKey;
    private final String uploadId;
    private final String tenantKey;
    
    public QBankDBQuestion() {
        this.id = 0;
        this.docId = null;
        this.courseId = 0;
        this.specifiedQid = null;
        this.subjectId = 0;
        this.topicId = 0;
        this.subTopicId = 0;
        this.level = 0;
        this.type = null;
        this.answer = null;
        this.qDocKey = null;
        this.uploadId = null;
        this.tenantKey = null;
    }
    
    public QBankDBQuestion(final int id, final String docId, final int course, final String specifiedQid, final int subjectId, final int topicId, final int subTopicOd, final int level, final String type, final String answer, final String qDocKey, final String uploadId, final String tenantKey) {
        this.id = id;
        this.docId = docId;
        this.courseId = course;
        this.specifiedQid = specifiedQid;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicOd;
        this.level = level;
        this.type = type;
        this.answer = answer;
        this.qDocKey = qDocKey;
        this.uploadId = uploadId;
        this.tenantKey = tenantKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getDocId() {
        return this.docId;
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
    
    public int getLevel() {
        return this.level;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getAnswer() {
        return this.answer;
    }
    
    public String getqDocKey() {
        return this.qDocKey;
    }
    
    public String getUploadId() {
        return this.uploadId;
    }
    
    public int getCourseId() {
        return this.courseId;
    }
    
    public String getSpecifiedQid() {
        return this.specifiedQid;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
