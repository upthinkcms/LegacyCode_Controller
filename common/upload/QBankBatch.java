// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import com.upthinkexperts.common.parsing.DocPart;
import java.util.List;
import java.util.Map;
import com.yojito.minima.logging.MinimaLogger;

public class QBankBatch
{
    private static final MinimaLogger LOGGER;
    private Map<String, List<Integer>> docToQBankMap;
    private Map<String, String> docNameToDocIdMap;
    private Map<String, String> docIdToFileMap;
    private Map<String, Map<String, List<DocPart.DocRun>>> imageMap;
    private Map<String, Map<String, Pair<String, Integer>>> imageReverseMap;
    private Map<String, String> qdocKeyMap;
    private Set<Pair<String, String>> parsingFailedDocList;
    
    public QBankBatch() {
        this.docToQBankMap = new HashMap<String, List<Integer>>();
        this.docNameToDocIdMap = new HashMap<String, String>();
        this.docIdToFileMap = new HashMap<String, String>();
        this.imageMap = new HashMap<String, Map<String, List<DocPart.DocRun>>>();
        this.imageReverseMap = new HashMap<String, Map<String, Pair<String, Integer>>>();
        this.qdocKeyMap = new HashMap<String, String>();
        this.parsingFailedDocList = new HashSet<Pair<String, String>>();
    }
    
    public synchronized void registerFileDocId(final String fileName, final String docId) {
        QBankBatch.LOGGER.debug("registerFileDocId Doc - %s -> %s", new Object[] { fileName, docId });
        final String docName = fileName.substring(0, fileName.length() - 5);
        this.docIdToFileMap.put(docId, fileName);
        this.docNameToDocIdMap.put(docName, docId);
    }
    
    public synchronized void registerParsingFailure(final String docName, final String docId) {
        QBankBatch.LOGGER.debug("Marking doc %s as failed during parsing. It will be ignored for further processing", new Object[] { docName });
        this.parsingFailedDocList.add((Pair<String, String>)Pair.of((Object)docName, (Object)docId));
    }
    
    public synchronized boolean hasDocParsingFailedForDoc(final String docName) {
        return this.parsingFailedDocList.stream().filter(p -> ((String)p.getLeft()).equals(docName)).findAny().isPresent();
    }
    
    public synchronized boolean hasDocParsingFailedForDocId(final String docId) {
        return this.parsingFailedDocList.stream().filter(p -> ((String)p.getRight()).equals(docId)).findAny().isPresent();
    }
    
    public synchronized void addQuestion(final String docId, final Integer qId) {
        QBankBatch.LOGGER.debug("Adding question to QBANK Doc - %s -> %d", new Object[] { docId, qId });
        List<Integer> qBank = this.docToQBankMap.get(docId);
        if (qBank == null) {
            qBank = new ArrayList<Integer>();
            this.docToQBankMap.put(docId, qBank);
        }
        qBank.add(qId);
    }
    
    public synchronized void addQuestionDocKey(final String docId, final int id, final String getqDocKey) {
        this.qdocKeyMap.put(String.format("%s/%d", docId, id), getqDocKey);
    }
    
    public synchronized String getQuestionDocKey(final String docId, final int id) {
        return this.qdocKeyMap.get(String.format("%s/%d", docId, id));
    }
    
    public synchronized List<Integer> getQuestions(final String docId) {
        QBankBatch.LOGGER.debug("DocToQbankMap Keys : " + this.docToQBankMap.keySet() + " docId Requested -" + docId, new Object[0]);
        return this.docToQBankMap.get(docId);
    }
    
    public synchronized void addImageMap(final String docId, final Map<Pair<String, Integer>, List<DocPart.DocRun>> _imageMap) {
        _imageMap.forEach((pair, list) -> {
            final String imageDocId = (String)pair.getLeft();
            final int qId = (int)pair.getRight();
            if (docId.equals(imageDocId)) {
                Map<String, Pair<String, Integer>> docImageMap = this.imageReverseMap.get(docId);
                if (docImageMap == null) {
                    docImageMap = new HashMap<String, Pair<String, Integer>>();
                    this.imageReverseMap.put(docId, docImageMap);
                }
                final Map<String, Pair<String, Integer>> finalDocImageMap = docImageMap;
                list.forEach(l -> {
                    switch (l.getType()) {
                        case IMAGE: {
                            finalDocImageMap.put(l.getText(), pair);
                            break;
                        }
                        case P: {
                            this.addImageEntryForParagraph(l, finalDocImageMap, (Pair<String, Integer>)pair);
                            break;
                        }
                    }
                });
            }
        });
    }
    
    public synchronized Map<String, Pair<String, Integer>> getImageMap(final String docId) {
        QBankBatch.LOGGER.debug("getImageMap for %s - %s, keys - %s\n", new Object[] { docId, this.imageReverseMap.get(docId), this.imageReverseMap.keySet() });
        return this.imageReverseMap.get(docId);
    }
    
    private synchronized void addImageEntryForParagraph(final DocPart.DocRun p, final Map<String, Pair<String, Integer>> finalDocImageMap, final Pair<String, Integer> pair) {
        p.getParagraphs().forEach(docRun -> {
            switch (docRun.getType()) {
                case IMAGE: {
                    finalDocImageMap.put(docRun.getText(), pair);
                    break;
                }
            }
        });
    }
    
    public synchronized String getDocName(final String docId) {
        QBankBatch.LOGGER.debug("getDocName for %s - %s\n", new Object[] { docId, this.docIdToFileMap.get(docId) });
        return this.docIdToFileMap.get(docId);
    }
    
    public synchronized String getDocId(final String docName) {
        QBankBatch.LOGGER.debug("getDocId for %s - %s\n", new Object[] { docName, this.docNameToDocIdMap.get(docName) });
        return this.docNameToDocIdMap.get(docName);
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankBatch.class);
    }
}
