// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class QBankDoc extends GsonDto
{
    private final String docId;
    private final DocResources resources;
    private final List<QBankQuestion> questions;
    
    public QBankDoc(final String docId, final DocResources resources, final List<QBankQuestion> questions) {
        this.docId = docId;
        this.resources = resources;
        this.questions = questions;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public DocResources getResources() {
        return this.resources;
    }
    
    public List<QBankQuestion> getQuestions() {
        return this.questions;
    }
}
