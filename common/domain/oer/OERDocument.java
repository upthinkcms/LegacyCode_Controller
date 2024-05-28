// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain.oer;

import com.upthinkexperts.common.domain.DocResources;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class OERDocument extends GsonDto
{
    private final String docId;
    private final String title;
    private final String subject;
    private final String book;
    private final String topic;
    private final String subTopic;
    private final String indexingId;
    private final OERLearningObjective learningObjective;
    private final List<OERPrerequisite> prerequisiteList;
    private final List<OERContent> contentList;
    private final DocResources resources;
    private final List<OERSectionExerciseSet> exerciseSetList;
    private final OERReview review;
    
    public OERDocument(final String docId, final String title, final String subject, final String book, final String topic, final String subTopic, final String indexingId, final OERLearningObjective learningObjective, final List<OERPrerequisite> prerequisiteList, final List<OERContent> contentList, final DocResources resources, final List<OERSectionExerciseSet> exerciseSetList, final OERReview review) {
        this.docId = docId;
        this.title = title;
        this.subject = subject;
        this.book = book;
        this.topic = topic;
        this.subTopic = subTopic;
        this.indexingId = indexingId;
        this.learningObjective = learningObjective;
        this.prerequisiteList = prerequisiteList;
        this.contentList = contentList;
        this.resources = resources;
        this.exerciseSetList = exerciseSetList;
        this.review = review;
    }
    
    public String getDocId() {
        return this.docId;
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
    
    public String getIndexingId() {
        return this.indexingId;
    }
    
    public OERLearningObjective getLearningObjective() {
        return this.learningObjective;
    }
    
    public List<OERPrerequisite> getPrerequisiteList() {
        return this.prerequisiteList;
    }
    
    public List<OERContent> getContentList() {
        return this.contentList;
    }
    
    public DocResources getResources() {
        return this.resources;
    }
    
    public List<OERSectionExerciseSet> getExerciseSetList() {
        return this.exerciseSetList;
    }
    
    public OERReview getReview() {
        return this.review;
    }
    
    public String getBook() {
        return this.book;
    }
    
    public String getTitle() {
        return this.title;
    }
}
