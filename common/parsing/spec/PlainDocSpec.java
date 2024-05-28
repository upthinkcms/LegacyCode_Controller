// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;

public class PlainDocSpec implements ParsingSpec
{
    @Override
    public boolean checkHierarchy(final DocPartTypes parent, final DocPartTypes type) {
        return true;
    }
    
    @Override
    public CompositeDetails checkComposite(final String text, final String qId) {
        return new CompositeDetails(false, null, null, null);
    }
    
    @Override
    public boolean isMarker(final String otext) {
        return false;
    }
    
    @Override
    public boolean isMarkerStart(final String otext) {
        return false;
    }
    
    @Override
    public boolean isMarkerEnd(final String otext) {
        return false;
    }
    
    @Override
    public String getName() {
        return "PLAIN";
    }
}
