// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import org.apache.lucene.document.Document;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import java.util.Iterator;
import com.upthinkexperts.db.FileDB;
import org.apache.lucene.queryparser.classic.ParseException;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import com.upthinkexperts.common.util.TextIndex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.File;

public class BatchParsing
{
    public static void main(final String[] args) throws IOException, URISyntaxException, ParseException {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/docs/Chemistry-20200723T074345Z-001/Chemistry";
        final File dir = new File(directory);
        int index = 1;
        final List<String> errorFiles = new ArrayList<String>();
        final Set<String> processedDocIds = new HashSet<String>();
        final Set<String> errorDocIds = new HashSet<String>();
        final Map<String, ParsedDoc> docMap = new HashMap<String, ParsedDoc>();
        final Map<String, ParsedDoc> questionDocMap = new HashMap<String, ParsedDoc>();
        final List<String> questionDocIds = new ArrayList<String>();
        final List<String> answerDocIds = new ArrayList<String>();
        final TextIndex textIndex = TextIndex.getOrCreateInstance("filedb");
        final String[] list = dir.list((d, f) -> f.endsWith(".docx"));
        for (int length = list.length, i = 0; i < length; ++i) {
            final String docFile = list[i];
            System.out.printf("\n\n %s >>>>>>>>>>>>>> %s <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n\n", index, docFile);
            final String nameWithoutExetension = docFile.substring(0, docFile.length() - ".docx".length());
            System.out.printf("nameWithoutExetension = %s\n", nameWithoutExetension);
            final String[] a = nameWithoutExetension.split("-");
            if (a[a.length - 1].equals("Question")) {
                System.out.printf("Not reading %s\n", docFile);
            }
            else {
                boolean isQuestion = false;
                boolean isAnswer = false;
                String docId;
                if (nameWithoutExetension.contains("Question-digital") || nameWithoutExetension.contains("Question-Digital")) {
                    isQuestion = true;
                    isAnswer = false;
                    docId = nameWithoutExetension.substring(0, nameWithoutExetension.length() - "-Question-digital".length());
                    System.out.printf("DocId =%s and its question\n", docId);
                    questionDocIds.add(docId);
                }
                else {
                    isAnswer = true;
                    isQuestion = false;
                    docId = nameWithoutExetension;
                    System.out.printf("DocId =%s and its answer\n", docId);
                    answerDocIds.add(docId);
                }
                try {
                    final PoiDocParser docParser = new PoiDocParser();
                    final String tempPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/olebin/" + docId;
                    final String imagesPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/images/" + docId;
                    final ParsedDoc parsedDoc = docParser.parse(docId, !isAnswer, ParsingSpecs.FORMAT2, new File(dir, docFile), tempPathPrefix, imagesPathPrefix);
                    if (isAnswer) {
                        docMap.put(docId, parsedDoc);
                    }
                    else {
                        questionDocMap.put(docId, parsedDoc);
                    }
                    processedDocIds.add(docId);
                }
                catch (final Exception e) {
                    System.out.println("Error for [" + docFile + "] -" + e.getMessage());
                    errorFiles.add(docFile);
                    errorDocIds.add(docId);
                }
                ++index;
            }
        }
        processedDocIds.removeAll(errorDocIds);
        final List<String> notInserted = insertDocuments(processedDocIds, docMap, questionDocMap);
        System.out.println("Failed Files -" + errorFiles);
        System.out.println("DocIds with errors -" + errorDocIds);
        System.out.printf("Questions = %d, Answers = %d\n", questionDocIds.size(), answerDocIds.size());
        Collections.sort(questionDocIds);
        Collections.sort(answerDocIds);
        System.out.printf("Following docIds are not inserted = %s\n", notInserted);
        TextIndex.getInstance().getDefaultIndex().searchIndex("text", "glucuronides").forEach(document -> System.out.println("QR docId -> " + document.get("docId")));
    }
    
    private static List<String> insertDocuments(final Set<String> processedDocIds, final Map<String, ParsedDoc> docMap, final Map<String, ParsedDoc> questionDocMap) throws IOException, URISyntaxException {
        final List<String> unprocessedIds = new ArrayList<String>();
        int count = 0;
        for (String docId : processedDocIds) {
            final ParsedDoc question = questionDocMap.get(docId);
            final ParsedDoc answer = docMap.get(docId);
            if (question == null || answer == null) {
                System.out.println(docId + " Null ->" + ((question == null) ? " question" : "") + ((answer == null) ? " answer" : ""));
                unprocessedIds.add(docId);
            }
            else {
                ++count;
                final String textBuilder = ParsedDoc.extractText(question);
                final SubjectDocConverter converter = new SubjectDocConverter("Chemistry", "Organic Chemistry", "thioesters", "EASY");
                final KnowledgeDoc knowledgeDoc = converter.convert(docId, question, answer);
                FileDB.insert(docId, knowledgeDoc);
                TextIndex.getInstance().getDefaultIndex().addFileToIndex(docId, textBuilder);
                System.out.printf("%s -> Adding docId = %s\n", count, docId);
            }
        }
        return unprocessedIds;
    }
}
