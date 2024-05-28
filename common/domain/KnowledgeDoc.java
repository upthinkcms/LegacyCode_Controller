// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class KnowledgeDoc extends GsonDto
{
    private final String docId;
    private final DocSection content;
    private final boolean active;
    private final String subject;
    private final String topic;
    private final String subTopic;
    private final String difficulty;
    private final List<String> tags;
    private final DocResources resources;
    
    public KnowledgeDoc(final String docId, final DocSection content, final boolean active, final String subject, final String topic, final String subTopic, final String difficulty, final List<String> tags, final DocResources resources) {
        this.docId = docId;
        this.content = content;
        this.active = active;
        this.subject = subject;
        this.topic = topic;
        this.subTopic = subTopic;
        this.difficulty = difficulty;
        this.tags = tags;
        this.resources = resources;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public DocSection getContent() {
        return this.content;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public String getTopic() {
        return this.topic;
    }
    
    public String getSubTopic() {
        return this.subTopic;
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }
    
    public List<String> getTags() {
        return this.tags;
    }
    
    public DocResources getResources() {
        return this.resources;
    }
}
