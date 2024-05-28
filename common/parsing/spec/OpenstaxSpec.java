// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;

public class OpenstaxSpec implements ParsingSpec
{
    @Override
    public boolean checkHierarchy(final DocPartTypes parent, final DocPartTypes type) {
        if (parent == null) {
            System.out.println("checkHierarchy :: Parent null  type " + type);
            return true;
        }
        System.out.println("checkHierarchy :: Parent Type " + parent + " type " + type);
        switch (type) {
            case openstax_document: {
                return parent.equals(DocPartTypes.START);
            }
            case openstax_learningobjective: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            case openstax_prerequisite: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            case openstax_content: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            case openstax_definition: {
                return parent.equals(DocPartTypes.openstax_content);
            }
            case openstax_example: {
                return parent.equals(DocPartTypes.openstax_content);
            }
            case openstax_practiseexample: {
                return parent.equals(DocPartTypes.openstax_content);
            }
            case openstax_bookmarks: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            case openstax_exerciseset: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            case openstax_exercise: {
                return parent.equals(DocPartTypes.openstax_exerciseset);
            }
            case openstax_review: {
                return parent.equals(DocPartTypes.openstax_document);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public CompositeDetails checkComposite(final String text, final String qId) {
        return new CompositeDetails(false, null, null, null);
    }
    
    @Override
    public boolean isMarker(final String otext) {
        final String text = otext.trim();
        final boolean match = text.startsWith("<") && text.endsWith(">") && (DocPartTypes.openstax_document.isContainedWithin(text) || DocPartTypes.openstax_learningobjective.isContainedWithin(text) || DocPartTypes.openstax_prerequisite.isContainedWithin(text) || DocPartTypes.openstax_content.isContainedWithin(text) || DocPartTypes.openstax_definition.isContainedWithin(text) || DocPartTypes.openstax_example.isContainedWithin(text) || DocPartTypes.openstax_practiseexample.isContainedWithin(text) || DocPartTypes.openstax_bookmarks.isContainedWithin(text) || DocPartTypes.openstax_exerciseset.isContainedWithin(text) || DocPartTypes.openstax_exercise.isContainedWithin(text) || DocPartTypes.openstax_review.isContainedWithin(text));
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
        return "OPENSTAX";
    }
}
