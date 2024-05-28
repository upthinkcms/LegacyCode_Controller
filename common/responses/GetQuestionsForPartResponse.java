// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.QBankDBQuestion;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class GetQuestionsForPartResponse extends GsonDto
{
    private final List<QBankDBQuestion> questionList;
    
    public GetQuestionsForPartResponse(final List<QBankDBQuestion> questionList) {
        this.questionList = questionList;
    }
    
    public GetQuestionsForPartResponse() {
        this.questionList = null;
    }
    
    public List<QBankDBQuestion> getQuestionList() {
        return this.questionList;
    }
}
