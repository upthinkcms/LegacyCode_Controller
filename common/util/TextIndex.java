// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.util.Iterator;
import java.nio.file.Path;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.yojito.minima.logging.MinimaLogger;

public class TextIndex
{
    private static final MinimaLogger LOGGER;
    private static TextIndex _instance;
    private final String rootPath;
    private final List<String> subjects;
    private final Map<IndexId, SubjectIndex> subjectIndexMap;
    private final IndexId DEFAULT_ID;
    
    public TextIndex(final String rootPath, final List<String> subjects) {
        this.DEFAULT_ID = new IndexId("DEFAULT", "Default");
        this.rootPath = rootPath;
        this.subjects = subjects;
        this.subjectIndexMap = new HashMap<IndexId, SubjectIndex>();
        if (subjects != null) {
            subjects.forEach(subject -> {
                try {
                    this.openOrCreateIndex(subject, true);
                    this.openOrCreateIndex(subject, false);
                }
                catch (final IOException e2) {
                    throw new RuntimeException(e2);
                }
                return;
            });
        }
        final Path indexPath = Paths.get(rootPath, "DEFAULT");
        try {
            this.subjectIndexMap.put(this.DEFAULT_ID, new SubjectIndex(this.DEFAULT_ID, (Directory)FSDirectory.open(indexPath), new StandardAnalyzer()));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void openOrCreateIndex(final String subject, final boolean isQuestion) throws IOException {
        final IndexId indexId = new IndexId(subject, isQuestion ? "Question" : "Answer");
        final Path indexPath = Paths.get(this.rootPath, subject, isQuestion ? "questionsIndex" : "answersIndex");
        indexPath.toFile().mkdirs();
        final SubjectIndex index = new SubjectIndex(indexId, (Directory)FSDirectory.open(indexPath), new StandardAnalyzer());
        this.subjectIndexMap.put(indexId, index);
        if (indexPath.toFile().list().length < 1) {
            index.deleteIndex();
        }
    }
    
    public static TextIndex getOrCreateInstance(final String rootPath) throws IOException {
        return getOrCreateInstance(rootPath, null);
    }
    
    public static TextIndex getOrCreateInstance(final String rootPath, final List<String> subjects) throws IOException {
        synchronized (TextIndex.class) {
            if (TextIndex._instance == null) {
                TextIndex._instance = new TextIndex(rootPath, subjects);
            }
        }
        return TextIndex._instance;
    }
    
    public static TextIndex getInstance() {
        return TextIndex._instance;
    }
    
    public SubjectIndex getDefaultIndex() {
        return this.subjectIndexMap.get(this.DEFAULT_ID);
    }
    
    public SubjectIndex getSubjectIndex(final IndexId indexId) {
        return this.subjectIndexMap.get(indexId);
    }
    
    public void deleteAllSubjectIndexes() throws IOException {
        for (final SubjectIndex index : this.subjectIndexMap.values()) {
            index.deleteIndex();
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)TextIndex.class);
    }
}
