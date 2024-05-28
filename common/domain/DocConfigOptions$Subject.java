// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public static class Subject extends GsonDto
{
    private final String name;
    private final String shortName;
    private final List<SubjectCategory> categories;
    private int subjectId;
    private int parentId;
    
    public Subject(final String name, final String shortName, final List<SubjectCategory> categories) {
        this.subjectId = 0;
        this.name = name;
        this.shortName = shortName;
        this.categories = categories;
    }
    
    public Subject(final String name, final String shortName, final List<SubjectCategory> categories, final int id, final int parentId) {
        this(name, shortName, categories);
        this.subjectId = id;
        this.parentId = parentId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    public List<SubjectCategory> getCategories() {
        return this.categories;
    }
    
    public Subject withId(final int id, final int parentId) {
        return new Subject(this.name, this.shortName, this.categories, id, parentId);
    }
}
