// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import com.upthinkexperts.common.domain.DocSectionType;
import com.upthinkexperts.common.domain.DocSection;
import com.upthinkexperts.common.domain.DocResources;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import java.util.ArrayList;
import java.util.List;

public class SubjectDocConverter
{
    private final boolean active = true;
    private String subject;
    private String topic;
    private String subTopic;
    private String difficulty;
    private final List<String> tags;
    
    public SubjectDocConverter(final String subject, final String topic, final String subTopic, final String difficulty) {
        this.tags = new ArrayList<String>();
        this.subject = subject;
        this.topic = topic;
        this.subTopic = subTopic;
        this.difficulty = difficulty;
    }
    
    public KnowledgeDoc convert(final String docId, final ParsedDoc question, final ParsedDoc answer) {
        final DocSection content = this.createDoc(question, answer);
        final KnowledgeDoc knowledgeDoc = new KnowledgeDoc(docId, content, true, this.subject, this.topic, this.subTopic, this.difficulty, this.tags, new DocResources(""));
        return knowledgeDoc;
    }
    
    private DocSection createDoc(final ParsedDoc question, final ParsedDoc answer) {
        final DocSection questionSection = this.toSection(question, DocSectionType.Question, true);
        final DocSection answerSection = this.toSection(answer, DocSectionType.Answer, false);
        return new DocSection(DocSectionType.Doc, Arrays.asList(questionSection, answerSection), null);
    }
    
    private DocSection toSection(final ParsedDoc question, final DocSectionType type, final boolean isQuestion) {
        final List<DocPart.DocRun> paragraphs = new ArrayList<DocPart.DocRun>();
        final List<DocSection> sections = new ArrayList<DocSection>();
        if (question.getParts() != null) {
            for (final DocPart p : question.getParts()) {
                if (p.getType().equals(DocPartTypes.START) || p.getType().equals(DocPartTypes.OUTLINE)) {
                    if (isQuestion) {
                        paragraphs.addAll(p.getParagraphs());
                        if (p.getChildren() == null || p.getChildren().isEmpty()) {
                            continue;
                        }
                        for (final DocPart part : p.getChildren()) {
                            switch (part.getType()) {
                                case OUTLINE: {
                                    paragraphs.addAll(part.getParagraphs());
                                    continue;
                                }
                                default: {
                                    throw new RuntimeException("Only OUTLINE sections allowed in Question");
                                }
                            }
                        }
                    }
                    else {
                        paragraphs.addAll(p.getParagraphs());
                        if (p.getChildren() == null || p.getChildren().isEmpty()) {
                            continue;
                        }
                        p.getChildren().forEach(d -> sections.add(this.docPartToDocSection(d)));
                    }
                }
                else {
                    sections.add(this.docPartToDocSection(p));
                }
            }
        }
        return new DocSection(type, sections, paragraphs);
    }
    
    private DocSection docPartToDocSection(final DocPart docPart) {
        DocSectionType type = DocSectionType.Interpretation;
        switch (docPart.getType()) {
            case ANSWER: {
                type = DocSectionType.Answer;
                break;
            }
            case SUB_PART: {
                type = DocSectionType.SubPart;
                break;
            }
            case CONCLUSION: {
                type = DocSectionType.Conclusion;
                break;
            }
            case EXPLANATION: {
                type = DocSectionType.Explanation;
                break;
            }
            case TO_DETERMINE: {
                type = DocSectionType.ToDetermine;
                break;
            }
            case INTERPRETATION_INTRODUCTION: {
                type = DocSectionType.Interpretation;
                break;
            }
            case SUMMARY_INTRODUCTION: {
                type = DocSectionType.SummaryIntroduction;
                break;
            }
        }
        List<DocSection> sections = null;
        if (docPart.getChildren() != null && docPart.getChildren().size() > 0) {
            sections = docPart.getChildren().stream().map(d -> this.docPartToDocSection(d)).collect((Collector<? super Object, ?, List<DocSection>>)Collectors.toList());
        }
        final List<DocPart.DocRun> trimmedParagraph = docPart.getParagraphs().stream().filter(s -> {
            switch (s.getType()) {
                case TEXT: {
                    return s.getText().trim().length() > 0;
                }
                default: {
                    return true;
                }
            }
        }).collect((Collector<? super Object, ?, List<DocPart.DocRun>>)Collectors.toList());
        return new DocSection(type, sections, trimmedParagraph);
    }
}
