// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class QBankQuestion extends GsonDto
{
    private final int id;
    private final String docId;
    private final String course;
    private final String specifiedQid;
    private final String subject;
    private final String topic;
    private final String subTopic;
    private final String level;
    private final String type;
    private final DocSection question;
    private final List<QBankOption> optionList;
    private final String answer;
    private final DocSection generalFeedback;
    private final List<QBankOption> specificFeebackList;
    private final String tenantKey;
    
    public QBankQuestion(final int id, final String docId, final String course, final String specifiedQid, final String subject, final String topic, final String subTopic, final String level, final String type, final DocSection question, final List<QBankOption> optionList, final String answer, final DocSection generalFeedback, final List<QBankOption> specificFeebackList, final String tenantKey) {
        this.id = id;
        this.docId = docId;
        this.course = course;
        this.specifiedQid = specifiedQid;
        this.subject = subject;
        this.topic = topic;
        this.subTopic = subTopic;
        this.level = level;
        this.type = type;
        this.question = question;
        this.optionList = optionList;
        this.answer = answer;
        this.generalFeedback = generalFeedback;
        this.specificFeebackList = specificFeebackList;
        this.tenantKey = tenantKey;
    }
    
    public int getId() {
        return this.id;
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
    
    public String getLevel() {
        return this.level;
    }
    
    public String getType() {
        return this.type;
    }
    
    public DocSection getQuestion() {
        return this.question;
    }
    
    public List<QBankOption> getOptionList() {
        return this.optionList;
    }
    
    public String getAnswer() {
        return this.answer;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public String getCourse() {
        return this.course;
    }
    
    public DocSection getGeneralFeedback() {
        return this.generalFeedback;
    }
    
    public List<QBankOption> getSpecificFeebackList() {
        return this.specificFeebackList;
    }
    
    public String getSpecifiedQid() {
        return this.specifiedQid;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
