// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload.oer;

import java.util.HashSet;
import java.util.HashMap;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Set;
import java.util.Map;
import com.yojito.minima.logging.MinimaLogger;

public class OERBatch
{
    private static final MinimaLogger LOGGER;
    private Map<String, String> docNameToDocIdMap;
    private Map<String, String> docIdToFileMap;
    private Set<Pair<String, String>> parsingFailedDocList;
    
    public OERBatch() {
        this.docNameToDocIdMap = new HashMap<String, String>();
        this.docIdToFileMap = new HashMap<String, String>();
        this.parsingFailedDocList = new HashSet<Pair<String, String>>();
    }
    
    public synchronized void registerFileDocId(final String fileName, final String docId) {
        OERBatch.LOGGER.debug("registerFileDocId Doc - %s -> %s", new Object[] { fileName, docId });
        final String docName = fileName.substring(0, fileName.length() - 5);
        this.docIdToFileMap.put(docId, fileName);
        this.docNameToDocIdMap.put(docName, docId);
    }
    
    public synchronized void registerParsingFailure(final String docName, final String docId) {
        OERBatch.LOGGER.debug("Marking doc %s as failed during parsing. It will be ignored for further processing", new Object[] { docName });
        this.parsingFailedDocList.add((Pair<String, String>)Pair.of((Object)docName, (Object)docId));
    }
    
    public synchronized boolean hasDocParsingFailedForDoc(final String docName) {
        return this.parsingFailedDocList.stream().filter(p -> ((String)p.getLeft()).equals(docName)).findAny().isPresent();
    }
    
    public synchronized boolean hasDocParsingFailedForDocId(final String docId) {
        return this.parsingFailedDocList.stream().filter(p -> ((String)p.getRight()).equals(docId)).findAny().isPresent();
    }
    
    public synchronized String getDocName(final String docId) {
        OERBatch.LOGGER.debug("getDocName for %s - %s\n", new Object[] { docId, this.docIdToFileMap.get(docId) });
        return this.docIdToFileMap.get(docId);
    }
    
    public synchronized String getDocId(final String docName) {
        OERBatch.LOGGER.debug("getDocId for %s - %s\n", new Object[] { docName, this.docNameToDocIdMap.get(docName) });
        return this.docNameToDocIdMap.get(docName);
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERBatch.class);
    }
}
