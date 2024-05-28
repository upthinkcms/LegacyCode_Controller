// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.upthinkexperts.common.parsing.DocPart;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class DocSection extends GsonDto
{
    private final DocSectionType type;
    private final List<DocSection> sections;
    final List<DocPart.DocRun> paragraphs;
    
    public DocSection(final DocSectionType type, final List<DocSection> sections, final List<DocPart.DocRun> paragraphs) {
        this.type = type;
        this.sections = sections;
        this.paragraphs = paragraphs;
    }
    
    public List<DocSection> getSections() {
        return this.sections;
    }
    
    public DocSectionType getType() {
        return this.type;
    }
    
    public List<DocPart.DocRun> getParagraphs() {
        return this.paragraphs;
    }
}
