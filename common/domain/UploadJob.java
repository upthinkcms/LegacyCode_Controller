// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonObject;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.gson.GsonDto;

public class UploadJob extends GsonDto
{
    private static final MinimaLogger LOGGER;
    private final String uuid;
    private final String fileName;
    private final UploadJobStatus status;
    private final ParsingSpec.DocFormat docFormat;
    private final String subject;
    private final String category;
    private final String subcategory;
    private final String difficulty;
    private final String url;
    private final String parsingSpec;
    private final long timestamp;
    private final GsonObject errorInfo;
    private final String tenantKey;
    private final int noOfFiles;
    
    public UploadJob(final String uuid, final String fileName, final UploadJobStatus status, final ParsingSpec.DocFormat docFormat, final String subject, final String category, final String subcategory, final String difficulty, final String url, final String parsingSpec, final long timestamp, final String tenantKey) {
        this.uuid = uuid;
        this.fileName = fileName;
        this.status = status;
        this.docFormat = docFormat;
        this.subject = subject;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.url = url;
        this.parsingSpec = parsingSpec;
        this.timestamp = timestamp;
        this.tenantKey = tenantKey;
        this.errorInfo = null;
        this.noOfFiles = 0;
    }
    
    public UploadJob(final String uploadId, final String fileName, final String status, final ParsingSpec.DocFormat docFormat, final String errorInfo, final String url, final long timestamp, final String tenantKey) {
        this.docFormat = docFormat;
        this.tenantKey = tenantKey;
        this.parsingSpec = null;
        this.timestamp = timestamp;
        this.uuid = uploadId;
        this.fileName = fileName;
        this.status = UploadJobStatus.valueOf(status);
        this.subject = null;
        this.category = null;
        this.subcategory = null;
        this.difficulty = null;
        this.url = url;
        this.errorInfo = new GsonObject(errorInfo);
        this.noOfFiles = 0;
    }
    
    public UploadJob(final String uploadId, final String fileName, final String status, final String info, final ParsingSpec.DocFormat docFormat, final String url, final String parsingSpec, final long timestamp, final String tenantKey) {
        this.docFormat = docFormat;
        this.parsingSpec = parsingSpec;
        this.timestamp = timestamp;
        this.tenantKey = tenantKey;
        final GsonObject gsonObject = new GsonObject(info);
        final String subject = gsonObject.optString("subject");
        final String category = gsonObject.optString("category");
        final String subcategory = gsonObject.optString("subcategory");
        final String difficulty = gsonObject.optString("difficulty");
        this.uuid = uploadId;
        this.fileName = fileName;
        this.status = UploadJobStatus.valueOf(status);
        this.subject = subject;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.url = url;
        this.errorInfo = null;
        this.noOfFiles = 0;
    }
    
    public UploadJob(final String uploadId, final String fileName, final String status, final String info, final ParsingSpec.DocFormat docFormat, final String url, final String parsingSpec, final long timestamp, final String tenantKey, final int noOfFiles) {
        this.docFormat = docFormat;
        this.parsingSpec = parsingSpec;
        this.timestamp = timestamp;
        this.tenantKey = tenantKey;
        final GsonObject gsonObject = new GsonObject(info);
        final String subject = gsonObject.optString("subject");
        final String category = gsonObject.optString("category");
        final String subcategory = gsonObject.optString("subcategory");
        final String difficulty = gsonObject.optString("difficulty");
        this.uuid = uploadId;
        this.fileName = fileName;
        this.status = UploadJobStatus.valueOf(status);
        this.subject = subject;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.url = url;
        this.errorInfo = null;
        this.noOfFiles = noOfFiles;
    }
    
    public GsonObject getInfo() {
        final GsonObject gsonObject = new GsonObject();
        gsonObject.put("subject", this.subject);
        gsonObject.put("category", this.category);
        gsonObject.put("subcategory", this.subcategory);
        gsonObject.put("difficulty", this.difficulty);
        return gsonObject;
    }
    
    public GsonObject getErrorInfo() {
        return this.errorInfo;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public int getNoOfFiles() {
        return this.noOfFiles;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public UploadJobStatus getStatus() {
        return this.status;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public String getSubcategory() {
        return this.subcategory;
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }
    
    public String getParsingSpec() {
        return this.parsingSpec;
    }
    
    public ParsingSpec.DocFormat getDocFormat() {
        return this.docFormat;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)UploadJob.class);
    }
    
    public enum UploadJobStatus
    {
        EMPTY, 
        SIGNED, 
        UPLOADING, 
        UPLOADED, 
        INDEXING, 
        INDEXED, 
        INDEXED_PROCESSED, 
        ERROR, 
        INDEXED_PROCESSED_WITH_ERROR;
    }
}
