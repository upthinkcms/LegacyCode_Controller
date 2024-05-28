// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.QBankDBQuestion;
import com.yojito.minima.gson.GsonDto;

public class ChangeQuestionForPartResponse extends GsonDto
{
    private final QBankDBQuestion question;
    
    public ChangeQuestionForPartResponse(final QBankDBQuestion question) {
        this.question = question;
    }
    
    public ChangeQuestionForPartResponse() {
        this.question = null;
    }
    
    public QBankDBQuestion getQuestion() {
        return this.question;
    }
}
