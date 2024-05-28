// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class QBankQuestionId extends GsonDto
{
    private final String docId;
    private final int qId;
    private final String tenantKey;
    
    public QBankQuestionId(final String docId, final int qId, final String tenantKey) {
        this.docId = docId;
        this.qId = qId;
        this.tenantKey = tenantKey;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public int getqId() {
        return this.qId;
    }
    
    public String getTenantKey() {
        return this.tenantKey;
    }
}
