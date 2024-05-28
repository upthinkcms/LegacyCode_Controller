// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain.oer;

import java.sql.Timestamp;
import com.yojito.minima.gson.GsonObject;
import com.yojito.minima.gson.GsonDto;

public class OERSegment extends GsonDto
{
    private final int id;
    private final int parentId;
    private final int subjectId;
    private final int topicId;
    private final int subTopicId;
    private final OERSegmentType type;
    private final String segment;
    private final String docId;
    private final String path;
    private final String tenantKey;
    private final String label;
    private final GsonObject attributes;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
    private final String jobId;
    
    public OERSegment(final int id, final int parentId, final int subjectId, final int topicId, final int subTopicId, final OERSegmentType type, final String segment, final String docId, final String path, final String tenantKey, final String label, final GsonObject attributes, final Timestamp createdAt, final Timestamp updatedAt, final String jobId) {
        this.id = id;
        this.parentId = parentId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.type = type;
        this.segment = segment;
        this.docId = docId;
        this.path = path;
        this.tenantKey = tenantKey;
        this.label = label;
        this.attributes = attributes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.jobId = jobId;
    }
    
    public OERSegment withId(final int newId) {
        return new OERSegment(newId, this.parentId, this.subjectId, this.topicId, this.subTopicId, this.type, this.segment, this.docId, this.path, this.tenantKey, this.label, this.attributes, this.createdAt, this.updatedAt, this.jobId);
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getParentId() {
        return this.parentId;
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
    
    public OERSegmentType getType() {
        return this.type;
    }
    
    public String getSegment() {
        return this.segment;
    }
    
    public GsonObject getAttributes() {
        return this.attributes;
    }
    
    public Timestamp getCreatedAt() {
        return this.createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public enum OERSegmentType
    {
        subject, 
        book, 
        topic, 
        subtopic, 
        segment;
    }
}
