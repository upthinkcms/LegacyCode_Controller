// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class DBSubject extends GsonDto
{
    private final String name;
    private final String shortName;
    private final int id;
    private final int parentId;
    
    public DBSubject(final String name, final String shortName, final int id, final int parentId) {
        this.name = name;
        this.shortName = shortName;
        this.id = id;
        this.parentId = parentId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getParentId() {
        return this.parentId;
    }
    
    public String toString() {
        return "DBSubject{name='" + this.name + "', shortName='" + this.shortName + "', id=" + this.id + ", parentId=" + this.parentId;
    }
}
