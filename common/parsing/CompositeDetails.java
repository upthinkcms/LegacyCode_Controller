// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

public class CompositeDetails
{
    private boolean isComposite;
    private DocPartTypes docPartTypes;
    private String text;
    private String qId;
    
    public CompositeDetails() {
    }
    
    public CompositeDetails(final boolean isComposite, final DocPartTypes docPartTypes, final String text, final String qId) {
        this.isComposite = isComposite;
        this.docPartTypes = docPartTypes;
        this.text = text;
        this.qId = qId;
    }
    
    public boolean isComposite() {
        return this.isComposite;
    }
    
    public DocPartTypes getDocPartTypes() {
        return this.docPartTypes;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getQId() {
        return this.qId;
    }
}
