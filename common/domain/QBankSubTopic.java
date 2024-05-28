// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class QBankSubTopic extends GsonDto
{
    private final int id;
    private final String name;
    private final int subjectId;
    private final int topicId;
    private final String tenantKey;
    
    public QBankSubTopic(final int id, final String name, final int subjectId, final int topicId, final String tenantKey) {
        this.id = id;
        this.name = name;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.tenantKey = tenantKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSubjectId() {
        return this.subjectId;
    }
    
    public int getTopicId() {
        return this.topicId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
