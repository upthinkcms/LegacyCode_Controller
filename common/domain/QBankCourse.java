// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class QBankCourse extends GsonDto
{
    private final int id;
    private final String name;
    private final List<Integer> subjectIds;
    private final String tenantKey;
    
    public QBankCourse(final int id, final String name, final List<Integer> subjectIds, final String tenantKey) {
        this.id = id;
        this.name = name;
        this.subjectIds = subjectIds;
        this.tenantKey = tenantKey;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Integer> getSubjectIds() {
        return this.subjectIds;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
