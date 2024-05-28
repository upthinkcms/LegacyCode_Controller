// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class QBankOption extends GsonDto
{
    private final String label;
    private final DocSection content;
    
    public QBankOption(final String label, final DocSection content) {
        this.label = label;
        this.content = content;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public DocSection getContent() {
        return this.content;
    }
}
