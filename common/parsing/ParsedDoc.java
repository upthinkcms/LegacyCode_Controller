// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;

public class ParsedDoc
{
    private final String docId;
    private final List<DocPart> parts;
    private final List<String> images;
    
    public ParsedDoc(final String docId, final List<DocPart> parts, final List<String> images) {
        this.docId = docId;
        this.parts = parts;
        this.images = images;
    }
    
    public void add(final DocPart docPart) {
        this.parts.add(docPart);
    }
    
    public List<DocPart> getParts() {
        return this.parts;
    }
    
    @Override
    public String toString() {
        return "ParsedDoc{docId='" + this.docId + "', parts=" + this.parts;
    }
    
    public static String extractText(final ParsedDoc question) {
        final StringBuilder stringBuilder = new StringBuilder();
        question.getParts().forEach(docPart -> appendDocPart(stringBuilder, docPart));
        return stringBuilder.toString();
    }
    
    private static void appendDocPart(final StringBuilder stringBuilder, final DocPart docPart) {
        appendPara(stringBuilder, docPart.getParagraphs());
        if (docPart.getChildren() != null && !docPart.getChildren().isEmpty()) {
            docPart.getChildren().forEach(part -> appendDocPart(stringBuilder, part));
        }
    }
    
    private static void appendPara(final StringBuilder stringBuilder, final List<DocPart.DocRun> paragraphs) {
        if (paragraphs != null && !paragraphs.isEmpty()) {
            paragraphs.forEach(s -> {
                if (s.getType().equals(DocPart.DocRunType.TEXT)) {
                    final String trimmed = s.getText().trim();
                    if (trimmed.length() > 0) {
                        stringBuilder.append(trimmed).append(" ");
                    }
                }
                if (s.getType().equals(DocPart.DocRunType.P)) {
                    appendPara(stringBuilder, s.getParagraphs());
                }
            });
        }
    }
    
    public List<String> getImages() {
        return this.images;
    }
    
    public String getDocId() {
        return this.docId;
    }
    
    public ParsedDoc withNewDocId(final String newDocId) {
        return new ParsedDoc(newDocId, this.parts, this.images);
    }
}
