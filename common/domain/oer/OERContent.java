// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain.oer;

import com.upthinkexperts.common.parsing.DocPart;
import java.util.Map;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class OERContent extends GsonDto
{
    private final List<OERNode> nodeList;
    private final Map<String, String> attributes;
    private final List<DocPart.DocRun> paragraphs;
    
    public OERContent(final List<OERNode> nodeList, final List<DocPart.DocRun> paragraphs, final Map<String, String> attributes) {
        this.nodeList = nodeList;
        this.attributes = attributes;
        this.paragraphs = paragraphs;
    }
    
    public List<OERNode> getNodeList() {
        return this.nodeList;
    }
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
    
    public List<DocPart.DocRun> getParagraphs() {
        return this.paragraphs;
    }
}
