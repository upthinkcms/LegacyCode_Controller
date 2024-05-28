// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.auth.domain.TokenValidation;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import com.yojito.minima.auth.domain.AuthenticatedRequest;

public class NewUploadRequest extends AuthenticatedRequest
{
    private final String fileName;
    private final String subject;
    private final String category;
    private final String subcategory;
    private final String difficulty;
    private final String parsingSpec;
    private final ParsingSpec.DocFormat docFormat;
    private final String tenantKey;
    private final int page;
    private final int limit;
    private final String sortField;
    private final String sortDirection;
    
    public NewUploadRequest(final TokenValidation id, final String fileName, final String subject, final String category, final String subcategory, final String difficulty, final String parsingSpec, final ParsingSpec.DocFormat docFormat, final String tenantKey) {
        super(id);
        this.fileName = fileName;
        this.subject = subject;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.parsingSpec = parsingSpec;
        this.docFormat = docFormat;
        this.tenantKey = tenantKey;
        this.page = 0;
        this.limit = 0;
        this.sortField = "";
        this.sortDirection = "";
    }
    
    public NewUploadRequest(final TokenValidation id, final String fileName, final String subject, final String category, final String subcategory, final String difficulty, final String parsingSpec, final ParsingSpec.DocFormat docFormat, final String tenantKey, final int page, final int limit, final String sortField, final String sortDirection) {
        super(id);
        this.fileName = fileName;
        this.subject = subject;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.parsingSpec = parsingSpec;
        this.docFormat = docFormat;
        this.tenantKey = tenantKey;
        this.page = page;
        this.limit = limit;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
    }
    
    public String getFileName() {
        return this.fileName;
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
    
    public int getPage() {
        return this.page;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public String getSortField() {
        return this.sortField;
    }
    
    public String getSortDirection() {
        return this.sortDirection;
    }
}
