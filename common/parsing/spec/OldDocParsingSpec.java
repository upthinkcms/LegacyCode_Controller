// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;

public class OldDocParsingSpec implements ParsingSpec
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
        final String text = otext.trim();
        final boolean match = (text.startsWith("<!--") || text.startsWith("</!--")) && text.endsWith("-->") && (DocPartTypes.ANSWER.isContainedWithin(text) || DocPartTypes.SUB_PART.isContainedWithin(text) || DocPartTypes.INTERPRETATION_INTRODUCTION.isContainedWithin(text) || DocPartTypes.EXPLANATION.isContainedWithin(text) || DocPartTypes.CONCLUSION.isContainedWithin(text));
        if (text.contains("<!")) {}
        return match;
    }
    
    @Override
    public boolean isMarkerStart(final String otext) {
        final String text = otext.trim();
        final boolean match = text.startsWith("<!--<") && !text.startsWith("<!--</");
        return match;
    }
    
    @Override
    public boolean isMarkerEnd(final String otext) {
        final String text = otext.trim();
        final boolean match = text.startsWith("</!--") || text.startsWith("<!--</");
        return match;
    }
    
    @Override
    public String getName() {
        return "FORMAT2";
    }
}
