// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class Task extends GsonDto
{
    private final int id;
    private final String name;
    private final String shortName;
    private final boolean isInternal;
    
    public Task(final int id, final String name, final String shortName, final boolean isInternal) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.isInternal = isInternal;
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
    
    public boolean isInternal() {
        return this.isInternal;
    }
}
