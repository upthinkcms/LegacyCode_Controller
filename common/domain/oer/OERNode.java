// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain.oer;

import java.util.Map;
import com.upthinkexperts.common.domain.DocSection;
import com.yojito.minima.gson.GsonDto;

public class OERNode extends GsonDto
{
    private final DocSection section;
    private final Map<String, String> attributes;
    
    public OERNode(final DocSection section, final Map<String, String> attributes) {
        this.section = section;
        this.attributes = attributes;
    }
    
    public DocSection getSection() {
        return this.section;
    }
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
}
