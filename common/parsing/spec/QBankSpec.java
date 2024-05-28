// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

import java.util.Iterator;
import com.upthinkexperts.common.util.CommonsUtil;
import com.upthinkexperts.common.parsing.CompositeDetails;
import com.upthinkexperts.common.parsing.DocPartTypes;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class QBankSpec implements ParsingSpec
{
    private Set<String> allowedTags;
    private Set<String> allowedTagsStart;
    private Set<String> allowedTagsEnd;
    
    public QBankSpec() {
        this.allowedTags = new HashSet<String>(Arrays.asList("docid", "questionanswer", "id", "course", "specifiedqid", "subject", "topic", "subtopic", "level", "type", "question", "options", "option", "label", "content", "answer", "generalfeedback", "specificfeedbacks", "specificfeedback"));
        this.allowedTagsStart = new HashSet<String>((Collection<? extends String>)this.allowedTags.stream().map(s -> "<" + s).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        this.allowedTagsEnd = new HashSet<String>((Collection<? extends String>)this.allowedTags.stream().map(s -> "</" + s).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
    }
    
    @Override
    public boolean checkHierarchy(final DocPartTypes parent, final DocPartTypes type) {
        if (parent == null) {
            System.out.println("checkHierarchy :: Parent null  type " + type);
            return true;
        }
        System.out.println("checkHierarchy :: Parent Type " + parent + " type " + type);
        switch (type) {
            case docid: {
                return parent.equals(DocPartTypes.START);
            }
            case questionanswer: {
                return parent.equals(DocPartTypes.START);
            }
            case subject: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case course: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case specifiedqid: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case topic: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case subtopic: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case level: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case type: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case options: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case option: {
                return parent.equals(DocPartTypes.options);
            }
            case label: {
                return parent.equals(DocPartTypes.option) || parent.equals(DocPartTypes.specificfeedback);
            }
            case content: {
                return parent.equals(DocPartTypes.option) || parent.equals(DocPartTypes.specificfeedback);
            }
            case answer: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case id: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case question: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case generalfeedback: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case specificfeedbacks: {
                return parent.equals(DocPartTypes.questionanswer);
            }
            case specificfeedback: {
                return parent.equals(DocPartTypes.specificfeedbacks);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public CompositeDetails checkComposite(final String text, String qId) {
        boolean startFound = false;
        boolean endFound = false;
        boolean idOpenFound = false;
        boolean idCloseFound = false;
        String startTag = null;
        String endTag = null;
        for (final String s : this.allowedTagsStart) {
            if (text.contains(s)) {
                startFound = true;
                startTag = s.substring(1, s.length() - 1);
                if (startTag.equals("id")) {
                    idOpenFound = true;
                    break;
                }
                break;
            }
        }
        for (final String s : this.allowedTagsEnd) {
            if (text.contains(s)) {
                endFound = true;
                endTag = s.substring(2, s.length() - 1);
                if (idOpenFound && endTag.equals("id")) {
                    idCloseFound = true;
                    break;
                }
                break;
            }
        }
        System.out.printf("StartTag : [%s] EndTag : [%s]\n", startTag, endTag);
        final boolean isComposite = startFound && endFound && startTag.equals(endTag);
        if (!isComposite) {
            return new CompositeDetails();
        }
        final DocPartTypes docPartTypes = DocPartTypes.getType(startTag);
        final int starting = startTag.length() + 2;
        final String stripped = CommonsUtil.trimWhitespace(text);
        final int ending = stripped.length() - (endTag.length() + 3);
        final String tagText = stripped.substring(starting, ending);
        if (idOpenFound && idCloseFound) {
            qId = tagText;
        }
        return new CompositeDetails(isComposite, docPartTypes, tagText, qId);
    }
    
    @Override
    public boolean isMarker(final String otext) {
        final String text = CommonsUtil.trimWhitespace(otext);
        final boolean match = text.startsWith("<") && text.endsWith(">") && (DocPartTypes.docid.isContainedWithin(text) || DocPartTypes.questionanswer.isContainedWithin(text) || DocPartTypes.id.isContainedWithin(text) || DocPartTypes.subject.isContainedWithin(text) || DocPartTypes.topic.isContainedWithin(text) || DocPartTypes.subtopic.isContainedWithin(text) || DocPartTypes.level.isContainedWithin(text) || DocPartTypes.topic.isContainedWithin(text) || DocPartTypes.type.isContainedWithin(text) || DocPartTypes.question.isContainedWithin(text) || DocPartTypes.options.isContainedWithin(text) || DocPartTypes.option.isContainedWithin(text) || DocPartTypes.label.isContainedWithin(text) || DocPartTypes.content.isContainedWithin(text) || DocPartTypes.answer.isContainedWithin(text) || DocPartTypes.course.isContainedWithin(text) || DocPartTypes.specifiedqid.isContainedWithin(text) || DocPartTypes.generalfeedback.isContainedWithin(text) || DocPartTypes.specificfeedback.isContainedWithin(text) || DocPartTypes.specificfeedbacks.isContainedWithin(text));
        if (text.contains("<!")) {}
        return match;
    }
    
    @Override
    public boolean isMarkerStart(final String otext) {
        final String text = CommonsUtil.trimWhitespace(otext);
        boolean startFound = false;
        for (final String s : this.allowedTagsStart) {
            if (text.contains(s)) {
                startFound = true;
                break;
            }
        }
        final boolean match = text.startsWith("<") && startFound;
        return match;
    }
    
    @Override
    public boolean isMarkerEnd(final String otext) {
        final String text = CommonsUtil.trimWhitespace(otext);
        boolean endFound = false;
        for (final String s : this.allowedTagsEnd) {
            if (text.contains(s)) {
                endFound = true;
                break;
            }
        }
        final boolean match = text.startsWith("</") && endFound;
        return match;
    }
    
    @Override
    public String getName() {
        return "QBANK";
    }
}
