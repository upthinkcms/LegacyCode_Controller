// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class GetQuestionsForPartRequest extends GsonDto
{
    private final int subjectId;
    private final List<Integer> topicIds;
    private final List<Integer> subTopicIds;
    private final String tenantKey;
    private final List<String> docIds;
    private final int noOfQuestions;
    private final String level;
    
    public GetQuestionsForPartRequest(final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final String tenantKey, final List<String> docIds, final int noOfQuestions, final String level) {
        this.subjectId = subjectId;
        this.topicIds = topicIds;
        this.subTopicIds = subTopicIds;
        this.tenantKey = tenantKey;
        this.docIds = docIds;
        this.noOfQuestions = noOfQuestions;
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
    
    public int getNoOfQuestions() {
        return this.noOfQuestions;
    }
    
    public String getLevel() {
        return this.level;
    }
}
