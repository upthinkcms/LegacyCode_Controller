// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

public class IndexId
{
    public static final String Q_TYPE = "Question";
    public static final String A_TYPE = "Answer";
    public static final String DEFAULT_TYPE = "Default";
    private final String subject;
    private final String type;
    
    public IndexId(final String subject, final String type) {
        this.subject = subject;
        this.type = type;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final IndexId indexId = (IndexId)o;
        return this.subject.equals(indexId.subject) && this.type.equals(indexId.type);
    }
    
    @Override
    public int hashCode() {
        int result = this.subject.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public String getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return "IndexId{subject='" + this.subject + "', type='" + this.type + "'}";
    }
}
