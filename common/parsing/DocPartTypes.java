// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public enum DocPartTypes
{
    START("START"), 
    SUB_PART("SUB-PART"), 
    INTERPRETATION_INTRODUCTION(new String[] { "INTERPRETATION-INTRODUCTION", "INTRODUCTION-INTERPRETATION" }), 
    ANSWER("ANSWER"), 
    EXPLANATION("EXPLANATION"), 
    CONCLUSION("CONCLUSION"), 
    OUTLINE(new String[] { "Q", "A" }), 
    TO_DETERMINE("TO-DETERMINE"), 
    SUMMARY_INTRODUCTION("SUMMARY-INTRODUCTION"), 
    END("END"), 
    docid("docid"), 
    questionanswer("questionanswer"), 
    id("id"), 
    course("course"), 
    specifiedqid("specifiedqid"), 
    subject("subject"), 
    topic("topic"), 
    subtopic("subtopic"), 
    level("level"), 
    type("type"), 
    question("question"), 
    options("options"), 
    option("option"), 
    label("label"), 
    content("content"), 
    answer("answer"), 
    specificfeedbacks("specificfeedbacks"), 
    specificfeedback("specificfeedback"), 
    generalfeedback("generalfeedback"), 
    openstax_document("openstax_document"), 
    openstax_learningobjective("openstax_learningobjective"), 
    openstax_prerequisite("openstax_prerequisite"), 
    openstax_content("openstax_content"), 
    openstax_definition("openstax_definition"), 
    openstax_example("openstax_example"), 
    openstax_practiseexample("openstax_practiseexample"), 
    openstax_bookmarks("openstax_bookmarks"), 
    openstax_exerciseset("openstax_exerciseset"), 
    openstax_exercise("openstax_exercise"), 
    openstax_review("openstax_review");
    
    Set<String> types;
    
    private DocPartTypes(final String[] mtypes) {
        this.types = new HashSet<String>(Arrays.asList(mtypes));
    }
    
    private DocPartTypes(final String type) {
        this.types = new HashSet<String>(Arrays.asList(type));
    }
    
    public boolean isContainedWithin(final String text) {
        for (final String s : this.types) {
            if (text.contains(s)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMatchingWithin(final String text) {
        for (final String s : this.types) {
            if (text.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public static DocPartTypes getType(final String oparagraphText) {
        final String paragraphText = oparagraphText.trim();
        if (DocPartTypes.openstax_document.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_document;
        }
        if (DocPartTypes.openstax_learningobjective.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_learningobjective;
        }
        if (DocPartTypes.openstax_prerequisite.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_prerequisite;
        }
        if (DocPartTypes.openstax_content.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_content;
        }
        if (DocPartTypes.openstax_definition.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_definition;
        }
        if (DocPartTypes.openstax_example.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_example;
        }
        if (DocPartTypes.openstax_practiseexample.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_practiseexample;
        }
        if (DocPartTypes.openstax_bookmarks.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_bookmarks;
        }
        if (DocPartTypes.openstax_exerciseset.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_exerciseset;
        }
        if (DocPartTypes.openstax_exercise.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_exercise;
        }
        if (DocPartTypes.openstax_review.isContainedWithin(paragraphText)) {
            return DocPartTypes.openstax_review;
        }
        if (DocPartTypes.specifiedqid.isContainedWithin(paragraphText)) {
            return DocPartTypes.specifiedqid;
        }
        if (DocPartTypes.SUB_PART.isContainedWithin(paragraphText)) {
            return DocPartTypes.SUB_PART;
        }
        if (DocPartTypes.ANSWER.isContainedWithin(paragraphText)) {
            return DocPartTypes.ANSWER;
        }
        if (DocPartTypes.INTERPRETATION_INTRODUCTION.isContainedWithin(paragraphText)) {
            return DocPartTypes.INTERPRETATION_INTRODUCTION;
        }
        if (DocPartTypes.EXPLANATION.isContainedWithin(paragraphText)) {
            return DocPartTypes.EXPLANATION;
        }
        if (DocPartTypes.CONCLUSION.isContainedWithin(paragraphText)) {
            return DocPartTypes.CONCLUSION;
        }
        if (DocPartTypes.TO_DETERMINE.isContainedWithin(paragraphText)) {
            return DocPartTypes.TO_DETERMINE;
        }
        if (DocPartTypes.SUMMARY_INTRODUCTION.isContainedWithin(paragraphText)) {
            return DocPartTypes.SUMMARY_INTRODUCTION;
        }
        if (DocPartTypes.OUTLINE.isContainedWithin(paragraphText)) {
            return DocPartTypes.OUTLINE;
        }
        if (DocPartTypes.docid.isContainedWithin(paragraphText)) {
            return DocPartTypes.docid;
        }
        if (DocPartTypes.questionanswer.isContainedWithin(paragraphText)) {
            return DocPartTypes.questionanswer;
        }
        if (DocPartTypes.id.isContainedWithin(paragraphText)) {
            return DocPartTypes.id;
        }
        if (DocPartTypes.subject.isContainedWithin(paragraphText)) {
            return DocPartTypes.subject;
        }
        if (DocPartTypes.subtopic.isContainedWithin(paragraphText)) {
            return DocPartTypes.subtopic;
        }
        if (DocPartTypes.topic.isContainedWithin(paragraphText)) {
            return DocPartTypes.topic;
        }
        if (DocPartTypes.level.isContainedWithin(paragraphText)) {
            return DocPartTypes.level;
        }
        if (DocPartTypes.type.isContainedWithin(paragraphText)) {
            return DocPartTypes.type;
        }
        if (DocPartTypes.question.isContainedWithin(paragraphText)) {
            return DocPartTypes.question;
        }
        if (DocPartTypes.options.isContainedWithin(paragraphText)) {
            return DocPartTypes.options;
        }
        if (DocPartTypes.option.isContainedWithin(paragraphText)) {
            return DocPartTypes.option;
        }
        if (DocPartTypes.label.isContainedWithin(paragraphText)) {
            return DocPartTypes.label;
        }
        if (DocPartTypes.content.isContainedWithin(paragraphText)) {
            return DocPartTypes.content;
        }
        if (DocPartTypes.answer.isContainedWithin(paragraphText)) {
            return DocPartTypes.answer;
        }
        if (DocPartTypes.course.isContainedWithin(paragraphText)) {
            return DocPartTypes.course;
        }
        if (DocPartTypes.generalfeedback.isContainedWithin(paragraphText)) {
            return DocPartTypes.generalfeedback;
        }
        if (DocPartTypes.specificfeedbacks.isContainedWithin(paragraphText)) {
            return DocPartTypes.specificfeedbacks;
        }
        if (DocPartTypes.specificfeedback.isContainedWithin(paragraphText)) {
            return DocPartTypes.specificfeedback;
        }
        throw new RuntimeException("Unknown type " + paragraphText);
    }
}
