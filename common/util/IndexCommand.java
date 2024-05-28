// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import com.upthinkexperts.common.domain.JobFile;
import com.upthinkexperts.common.domain.KnowledgeDoc;

public class IndexCommand
{
    private final String docId;
    private final String qContent;
    private final String aContent;
    private final KnowledgeDoc knowledgeDoc;
    private final JobFile question;
    private final JobFile answer;
    private final boolean isUpdate;
    
    public IndexCommand(final String docId, final String qContent, final String aContent, final KnowledgeDoc knowledgeDoc, final JobFile question, final JobFile answer, final boolean isUpdate) {
        this.docId = docId;
        this.qContent = qContent;
        this.aContent = aContent;
        this.knowledgeDoc = knowledgeDoc;
        this.question = question;
        this.answer = answer;
        this.isUpdate = isUpdate;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public String getqContent() {
        return this.qContent;
    }
    
    public String getaContent() {
        return this.aContent;
    }
    
    public KnowledgeDoc getKnowledgeDoc() {
        return this.knowledgeDoc;
    }
    
    public JobFile getQuestion() {
        return this.question;
    }
    
    public JobFile getAnswer() {
        return this.answer;
    }
    
    public boolean isUpdate() {
        return this.isUpdate;
    }
}
