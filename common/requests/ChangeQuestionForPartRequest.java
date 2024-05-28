// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.upthinkexperts.common.util.QuestionConfig;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class ChangeQuestionForPartRequest extends GsonDto
{
    private final int subjectId;
    private final List<Integer> topicIds;
    private final List<Integer> subTopicIds;
    private final String tenantKey;
    private final List<String> docIds;
    private final List<QuestionConfig> fixedQuestions;
    private final String level;
    
    public ChangeQuestionForPartRequest(final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final String tenantKey, final List<String> docIds, final List<QuestionConfig> fixedQuestions, final String level) {
        this.subjectId = subjectId;
        this.topicIds = topicIds;
        this.subTopicIds = subTopicIds;
        this.tenantKey = tenantKey;
        this.docIds = docIds;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
    }
    
    public int getSubjectId() {
        return this.subjectId;
    }
    
    public List<Integer> getTopicIds() {
        return this.topicIds;
    }
    
    public List<Integer> getSubTopicIds() {
        return this.subTopicIds;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
    
    public List<String> getDocIds() {
        return this.docIds;
    }
    
    public List<QuestionConfig> getFixedQuestions() {
        return this.fixedQuestions;
    }
    
    public String getLevel() {
        return this.level;
    }
}
