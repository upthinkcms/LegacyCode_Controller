// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public static class SubjectCategory extends GsonDto
{
    private final String name;
    private final List<String> subcategories;
    
    public SubjectCategory(final String name, final List<String> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getSubcategories() {
        return this.subcategories;
    }
}
