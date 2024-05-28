// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;

public interface ParsingSpec
{
    boolean checkHierarchy(final DocPartTypes p0, final DocPartTypes p1);
    
    CompositeDetails checkComposite(final String p0, final String p1);
    
    boolean isMarker(final String p0);
    
    boolean isMarkerStart(final String p0);
    
    boolean isMarkerEnd(final String p0);
    
    String getName();
    
    public enum DocFormat
    {
        CMS, 
        QBANK, 
        SCHEDULE_SHEET, 
        EMP_SHEET, 
        OER;
    }
}
