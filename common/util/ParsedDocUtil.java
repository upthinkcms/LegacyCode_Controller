// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import org.apache.commons.lang3.tuple.Pair;
import java.util.Iterator;
import java.util.Map;
import com.upthinkexperts.common.parsing.DocPart;
import java.util.HashMap;
import com.upthinkexperts.common.parsing.ParsedDoc;
import java.io.PrintStream;

public class ParsedDocUtil
{
    public static void printParsedDoc(final PrintStream out, final ParsedDoc doc) {
        final Map<Object, Boolean> visited = new HashMap<Object, Boolean>();
        visited.put(doc, true);
        out.println("DocID -> " + doc.getDocId());
        for (final DocPart docPart : doc.getParts()) {
            printDocPart(out, 0, docPart, visited);
        }
    }
    
    private static void printDocPart(final PrintStream out, final int n, final DocPart docPart, final Map<Object, Boolean> visited) {
        String prefix = "";
        for (int i = 0; i < n; ++i) {
            prefix = prefix;
        }
        if (visited.containsKey(docPart)) {
            throw new RuntimeException("Already visited" + docPart);
        }
        visited.put(docPart, true);
        out.printf("%s DocPart type=%s, paragraphs=[%d] children=[%d]\n", prefix, docPart.getType(), (docPart.getParagraphs() != null) ? docPart.getParagraphs().size() : 0, (docPart.getChildren() != null) ? docPart.getChildren().size() : 0);
        if (docPart.getParagraphs() != null) {
            for (final DocPart.DocRun para : docPart.getParagraphs()) {
                printDocRun(out, n + 1, para, visited);
            }
        }
        if (docPart.getChildren() != null) {
            for (final DocPart para2 : docPart.getChildren()) {
                printDocPart(out, n + 1, para2, visited);
            }
        }
    }
    
    private static void printDocRun(final PrintStream out, final int n, final DocPart.DocRun para, final Map<Object, Boolean> visited) {
        String prefix = "";
        for (int i = 0; i < n; ++i) {
            prefix = prefix;
        }
        if (visited.containsKey(para)) {
            throw new RuntimeException("Already visited" + para);
        }
        visited.put(para, true);
        out.printf("%s DocRun type=[%s] text=[%s] paragraphs=[%d]\n", prefix, para.getType(), para.getText(), (para.getParagraphs() != null) ? para.getParagraphs().size() : 0);
        if (para.getParagraphs() != null) {
            for (final DocPart.DocRun p : para.getParagraphs()) {
                printDocRun(out, n + 1, p, visited);
            }
        }
    }
    
    public static String PTrim(String input) {
        final Pair<String, String> spacePair = getPairWithLeadingSpaces((Pair<String, String>)Pair.of((Object)"", (Object)input));
        input = (String)spacePair.getRight();
        String trimmed = null;
        if (" ".equals(input)) {
            trimmed = input.trim();
        }
        else if (input.endsWith(" ")) {
            if (input.endsWith("  ")) {
                trimmed = input.trim();
            }
            else {
                trimmed = input;
            }
        }
        else {
            trimmed = input.trim();
        }
        if (trimmed.length() == 0) {
            return trimmed;
        }
        return (String)spacePair.getLeft() + trimmed;
    }
    
    private static Pair<String, String> getPairWithLeadingSpaces(final Pair<String, String> pair) {
        if (((String)pair.getRight()).startsWith(" ")) {
            return getPairWithLeadingSpaces((Pair<String, String>)Pair.of((String)pair.getLeft(), (Object)((String)pair.getRight()).substring(1)));
        }
        return pair;
    }
}
