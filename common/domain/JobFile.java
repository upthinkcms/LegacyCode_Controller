// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class JobFile extends GsonDto
{
    private final String docId;
    private final String jobId;
    private final String docFile;
    private final boolean isQuestion;
    private final boolean parsed;
    private final boolean indexed;
    private final boolean uploaded;
    private final String error;
    private final JobFileErrorCodes errorCode;
    private final String tenantKey;
    
    public JobFile(final String docId, final String jobId, final String docFile, final boolean isQuestion, final boolean parsed, final boolean indexed, final boolean uploaded, final String error, final JobFileErrorCodes errorCode, final String tenantKey) {
        this.docId = docId;
        this.jobId = jobId;
        this.docFile = docFile;
        this.isQuestion = isQuestion;
        this.parsed = parsed;
        this.indexed = indexed;
        this.uploaded = uploaded;
        this.error = error;
        this.errorCode = errorCode;
        this.tenantKey = tenantKey;
    }
    
    public JobFile(final String docId, final String jobId, final String docFile, final boolean isQuestion, final String tenantKey) {
        this.docId = docId;
        this.jobId = jobId;
        this.docFile = docFile;
        this.isQuestion = isQuestion;
        this.tenantKey = tenantKey;
        this.parsed = false;
        this.indexed = false;
        this.uploaded = false;
        this.error = null;
        this.errorCode = null;
    }
    
    public JobFile parsed() {
        return new JobFile(this.docId, this.jobId, this.docFile, this.isQuestion, true, this.indexed, this.uploaded, this.error, this.errorCode, this.tenantKey);
    }
    
    public JobFile indexed() {
        return new JobFile(this.docId, this.jobId, this.docFile, this.isQuestion, this.parsed, true, this.uploaded, this.error, this.errorCode, this.tenantKey);
    }
    
    public JobFile uploaded() {
        return new JobFile(this.docId, this.jobId, this.docFile, this.isQuestion, this.parsed, this.indexed, true, this.error, this.errorCode, this.tenantKey);
    }
    
    public JobFile addQuestion(final String _questionFile) {
        return new JobFile(this.docId, this.jobId, _questionFile, true, this.parsed, this.indexed, this.uploaded, this.error, this.errorCode, this.tenantKey);
    }
    
    public JobFile addAnswer(final String _answerFile) {
        return new JobFile(this.docId, this.jobId, this.docFile, false, this.parsed, this.indexed, this.uploaded, this.error, this.errorCode, this.tenantKey);
    }
    
    public JobFile error(final String error, final JobFileErrorCodes errorCode) {
        return new JobFile(this.docId, this.jobId, this.docFile, this.isQuestion, true, this.indexed, this.uploaded, error, errorCode, this.tenantKey);
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public String getJobId() {
        return this.jobId;
    }
    
    public String getDocFile() {
        return this.docFile;
    }
    
    public boolean isQuestion() {
        return this.isQuestion;
    }
    
    public boolean isParsed() {
        return this.parsed;
    }
    
    public boolean isIndexed() {
        return this.indexed;
    }
    
    public boolean isUploaded() {
        return this.uploaded;
    }
    
    public String getError() {
        return this.error;
    }
    
    public JobFileErrorCodes getErrorCode() {
        return this.errorCode;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
    
    public enum JobFileErrorCodes
    {
        QUESTION_FILE_MISSING, 
        ANSWER_FILE_MISSING, 
        QUESTION_FILE_PARSE_ERROR, 
        ANSWER_FILE_PARSE_ERROR, 
        UNKNOWN;
    }
}
