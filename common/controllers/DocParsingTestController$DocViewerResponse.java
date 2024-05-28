// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.controllers;

import com.upthinkexperts.common.domain.DocSection;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public static class DocViewerResponse extends GsonDto
{
    private final List<DocSection> sections;
    
    public DocViewerResponse(final List<DocSection> sections) {
        this.sections = sections;
    }
}
