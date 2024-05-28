// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;

public class NewSpec_Drop2 implements ParsingSpec
{
    @Override
    public boolean checkHierarchy(final DocPartTypes parent, final DocPartTypes type) {
        return true;
    }
    
    @Override
    public CompositeDetails checkComposite(final String text, final String qId) {
        return new CompositeDetails(false, null, null, null);
    }
    
    private boolean isOutLineMarker(final String otext) {
        final String text = otext.trim();
        boolean outlineMatch = false;
        if (text.startsWith("<")) {
            if (text.startsWith("</")) {
                outlineMatch = (text.endsWith(">") && DocPartTypes.OUTLINE.isMatchingWithin(text.substring(2, text.length() - 1)));
            }
            else {
                outlineMatch = (text.endsWith(">") && DocPartTypes.OUTLINE.isMatchingWithin(text.substring(1, text.length() - 1)));
            }
        }
        return outlineMatch;
    }
    
    @Override
    public boolean isMarker(final String otext) {
        final boolean outlineMatch = this.isOutLineMarker(otext);
        if (outlineMatch) {
            return true;
        }
        final String text = otext.trim();
        final boolean match = (text.startsWith("<") || text.startsWith("</")) && text.endsWith(">") && (DocPartTypes.ANSWER.isContainedWithin(text) || DocPartTypes.TO_DETERMINE.isContainedWithin(text) || DocPartTypes.ANSWER.isContainedWithin(text) || DocPartTypes.EXPLANATION.isContainedWithin(text) || DocPartTypes.CONCLUSION.isContainedWithin(text));
        if (text.contains("<!")) {}
        return match;
    }
    
    @Override
    public boolean isMarkerStart(final String otext) {
        final String text = otext.trim();
        final boolean match = text.startsWith("<") && !text.startsWith("</");
        return match;
    }
    
    @Override
    public boolean isMarkerEnd(final String otext) {
        final String text = otext.trim();
        final boolean match = text.startsWith("</");
        return match;
    }
    
    @Override
    public String getName() {
        return "FORMAT3";
    }
}
