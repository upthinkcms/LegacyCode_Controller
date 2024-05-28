// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Map;
import com.upthinkexperts.common.domain.DocSection;
import com.upthinkexperts.common.domain.DocSectionType;
import java.util.Iterator;
import com.upthinkexperts.common.util.CommonsUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

public class ConverterUtil
{
    public static DocPart getChildrenOfType(final DocPart qp, final DocPartTypes type) {
        return getChildrenOfType(qp, type, true);
    }
    
    public static DocPart getChildrenOfType(final DocPart qp, final DocPartTypes type, final boolean checkPresense) {
        final Optional<DocPart> idPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(type)).findFirst();
        if (checkPresense && idPart.isEmpty()) {
            throw new RuntimeException("Children of type - " + type + " is missing");
        }
        if (idPart.isPresent()) {
            return idPart.get();
        }
        return null;
    }
    
    public static List<DocPart> getAllChildrenOfType(final DocPart qp, final DocPartTypes type) {
        final List<DocPart> parts = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(type)).collect((Collector<? super Object, ?, List<DocPart>>)Collectors.toList());
        return parts;
    }
    
    public static String getTextOfChildrenOfType(final DocPart qp, final DocPartTypes type) {
        return retriveText(getChildrenOfType(qp, type, true));
    }
    
    public static String getTextOfChildrenOfType(final DocPart qp, final DocPartTypes type, final boolean checkPresence) {
        final DocPart docPart = getChildrenOfType(qp, type, checkPresence);
        if (docPart != null) {
            return retriveText(docPart);
        }
        return null;
    }
    
    public static String retriveText(final DocPart ps) {
        final StringBuilder sb = new StringBuilder();
        ps.getParagraphs().forEach(p -> {
            if (p.getText() != null) {
                sb.append(p);
            }
            if (p.getParagraphs() != null) {
                p.getParagraphs().iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final DocPart.DocRun t = iterator.next();
                    sb.append(t.getText());
                }
            }
            return;
        });
        return CommonsUtil.trimWhitespace(sb.toString());
    }
    
    public static DocSection toDocSection(final DocSectionType type, final DocPart questionPart) {
        List<DocSection> sections = null;
        if (questionPart.getChildren() != null) {
            sections = questionPart.getChildren().stream().map(s -> toDocSection(DocSectionType.SubPart, s)).collect((Collector<? super Object, ?, List<DocSection>>)Collectors.toList());
        }
        return new DocSection((type == null) ? DocSectionType.Doc : type, sections, questionPart.getParagraphs());
    }
    
    public static DocSection toDocSection(final DocSectionType type, final DocPart questionPart, final Map<String, String> rootAttributes) {
        List<DocSection> sections = null;
        if (questionPart.getChildren() != null) {
            sections = questionPart.getChildren().stream().map(s -> toDocSection(DocSectionType.SubPart, s)).collect((Collector<? super Object, ?, List<DocSection>>)Collectors.toList());
        }
        return new DocSection((type == null) ? DocSectionType.Doc : type, sections, questionPart.getParagraphs());
    }
}
