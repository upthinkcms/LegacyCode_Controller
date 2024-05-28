// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.auth.domain.TokenValidation;
import com.yojito.minima.auth.domain.AuthenticatedRequest;

public class DocParsingTestRequest extends AuthenticatedRequest
{
    private final String directory;
    private final String fileName;
    private final String docId;
    
    public DocParsingTestRequest(final TokenValidation id, final String directory, final String fileName, final String docId) {
        super(id);
        this.directory = directory;
        this.fileName = fileName;
        this.docId = docId;
    }
    
    public String getDirectory() {
        return this.directory;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getDocId() {
        return this.docId;
    }
}
