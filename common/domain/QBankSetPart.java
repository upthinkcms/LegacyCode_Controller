// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class QBankSetPart extends GsonDto
{
    private final int courseId;
    private final int subjectId;
    private final int topicId;
    private final int subTopicId;
    private final List<String> docIds;
    private final String setFormation;
    private final SetKind kind;
    private final int numQuestions;
    private final List<FixedQuestion> fixedQuestions;
    private final String level;
    private final String subjectName;
    private final String topicName;
    private final String subtopicName;
    private final String courseName;
    private final Map<Integer, String> topicIdsNames;
    private final Map<Integer, String> subTopicIdsNames;
    
    public String getSubjectName() {
        return this.subjectName;
    }
    
    public String getTopicName() {
        return this.topicName;
    }
    
    public String getSubtopicName() {
        return this.subtopicName;
    }
    
    public QBankSetPart(final int courseId, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.courseName = null;
        this.subjectName = null;
        this.topicName = null;
        this.subtopicName = null;
        final Map<Integer, String> tempTopicIdsNames = new HashMap<Integer, String>();
        tempTopicIdsNames.put(topicId, null);
        this.topicIdsNames = tempTopicIdsNames;
        final Map<Integer, String> tempSubTopicIdsNames = new HashMap<Integer, String>();
        tempSubTopicIdsNames.put(subTopicId, null);
        this.subTopicIdsNames = tempSubTopicIdsNames;
    }
    
    public QBankSetPart(final int courseId, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level, final Map<Integer, String> topicIdsNames, final Map<Integer, String> subTopicIdsNames) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.courseName = null;
        this.subjectName = null;
        this.topicName = null;
        this.subtopicName = null;
        this.topicIdsNames = topicIdsNames;
        this.subTopicIdsNames = subTopicIdsNames;
    }
    
    public QBankSetPart(final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level, final Map<Integer, String> topicIdsNames, final Map<Integer, String> subTopicIdsNames) {
        this.courseId = -1;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.courseName = null;
        this.subjectName = null;
        this.topicName = null;
        this.subtopicName = null;
        this.topicIdsNames = topicIdsNames;
        this.subTopicIdsNames = subTopicIdsNames;
    }
    
    public QBankSetPart(final int courseId, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level, final String subjectName, final String topicName, final String subtopicName, final String courseName) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.subjectName = subjectName;
        this.topicName = topicName;
        this.subtopicName = subtopicName;
        this.courseName = courseName;
        (this.topicIdsNames = new HashMap<Integer, String>()).put(topicId, topicName);
        (this.subTopicIdsNames = new HashMap<Integer, String>()).put(subTopicId, subtopicName);
    }
    
    public QBankSetPart(final int courseId, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level, final String subjectName, final String courseName, final Map<Integer, String> topicIdsNames, final Map<Integer, String> subTopicIdsNames) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.subjectName = subjectName;
        this.topicName = null;
        this.subtopicName = null;
        this.courseName = courseName;
        this.topicIdsNames = topicIdsNames;
        this.subTopicIdsNames = subTopicIdsNames;
    }
    
    public QBankSetPart(final int courseId, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final String setFormation, final SetKind kind, final int numQuestions, final List<FixedQuestion> fixedQuestions, final String level, final String subjectName, final String topicName, final String subtopicName, final String courseName, final Map<Integer, String> topicIdsNames, final Map<Integer, String> subTopicIdsNames) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.topicId = topicId;
        this.subTopicId = subTopicId;
        this.docIds = docIds;
        this.setFormation = setFormation;
        this.kind = kind;
        this.numQuestions = numQuestions;
        this.fixedQuestions = fixedQuestions;
        this.level = level;
        this.subjectName = subjectName;
        this.topicName = topicName;
        this.subtopicName = subtopicName;
        this.courseName = courseName;
        this.topicIdsNames = topicIdsNames;
        this.subTopicIdsNames = subTopicIdsNames;
    }
    
    public int getSubjectId() {
        return this.subjectId;
    }
    
    public int getTopicId() {
        return this.topicId;
    }
    
    public int getSubTopicId() {
        return this.subTopicId;
    }
    
    public List<String> getDocIds() {
        return this.docIds;
    }
    
    public String getSetFormation() {
        return this.setFormation;
    }
    
    public SetKind getKind() {
        return this.kind;
    }
    
    public int getNumQuestions() {
        return this.numQuestions;
    }
    
    public List<FixedQuestion> getFixedQuestions() {
        return this.fixedQuestions;
    }
    
    public String getLevel() {
        return this.level;
    }
    
    public int getCourseId() {
        return this.courseId;
    }
    
    public String getCourseName() {
        return this.courseName;
    }
    
    public Map<Integer, String> getTopicIdsNames() {
        return this.topicIdsNames;
    }
    
    public Map<Integer, String> getSubTopicIdsNames() {
        return this.subTopicIdsNames;
    }
    
    public QBankSetPart withNames(final String courseName, final String subjectName, final String topicName, final String subtopicName) {
        return new QBankSetPart(this.courseId, this.subjectId, this.topicId, this.subTopicId, this.docIds, this.setFormation, this.kind, this.numQuestions, this.fixedQuestions, this.level, subjectName, topicName, subtopicName, courseName);
    }
    
    public QBankSetPart withNames(final String courseName, final String subjectName) {
        return new QBankSetPart(this.courseId, this.subjectId, this.topicId, this.subTopicId, this.docIds, this.setFormation, this.kind, this.numQuestions, this.fixedQuestions, this.level, subjectName, courseName, this.topicIdsNames, this.subTopicIdsNames);
    }
    
    public enum SetKind
    {
        FIXED, 
        DYNAMIC;
    }
    
    public static class FixedQuestion extends GsonDto
    {
        private final String docId;
        private final int qId;
        private final int subjectId;
        private final int topicId;
        private final int subTopicId;
        
        public FixedQuestion(final String docId, final int qId, final int subjectId, final int topicId, final int subTopicId) {
            this.docId = docId;
            this.qId = qId;
            this.subjectId = subjectId;
            this.topicId = topicId;
            this.subTopicId = subTopicId;
        }
        
        public String getDocId() {
            return this.docId;
        }
        
        public int getQId() {
            return this.qId;
        }
        
        public int getSubjectId() {
            return this.subjectId;
        }
        
        public int getTopicId() {
            return this.topicId;
        }
        
        public int getSubTopicId() {
            return this.subTopicId;
        }
    }
}
