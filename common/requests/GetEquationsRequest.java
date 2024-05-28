// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.requests;

import com.yojito.minima.gson.GsonDto;

public class GetEquationsRequest extends GsonDto
{
    private final String docId;
    
    public GetEquationsRequest(final String docId) {
        this.docId = docId;
    }
    
    public String getDocId() {
        return this.docId;
    }
}
