// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class QBankTopic extends GsonDto
{
    private final int id;
    private final String name;
    private final int subjectId;
    private final String tenantKey;
    
    public QBankTopic(final int id, final String name, final int subjectId, final String tenantKey) {
        this.id = id;
        this.name = name;
        this.subjectId = subjectId;
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
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
