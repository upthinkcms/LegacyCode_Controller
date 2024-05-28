// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import java.util.ArrayList;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import java.util.List;
import java.net.URISyntaxException;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import com.yojito.minima.logging.MinimaLogger;

public class SubjectIndex
{
    private static final MinimaLogger LOGGER;
    private final IndexId indexId;
    private final Directory indexDirectory;
    private final StandardAnalyzer analyzer;
    private final ReentrantReadWriteLock indexLock;
    
    public SubjectIndex(final IndexId indexId, final Directory indexDirectory, final StandardAnalyzer analyzer) {
        this.indexLock = new ReentrantReadWriteLock();
        this.indexId = indexId;
        this.indexDirectory = indexDirectory;
        this.analyzer = analyzer;
        SubjectIndex.LOGGER.info("Opened subject index for %s[%s] from dir [%s]", new Object[] { indexId.getSubject(), indexId.getType(), indexDirectory });
    }
    
    public void deleteIndex() throws IOException {
        final Lock writeLock = this.indexLock.writeLock();
        try {
            writeLock.lock();
            final IndexWriterConfig indexWriterConfig = new IndexWriterConfig((Analyzer)this.analyzer);
            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            final IndexWriter indexWriter = new IndexWriter(this.indexDirectory, indexWriterConfig);
            indexWriter.close();
        }
        finally {
            writeLock.unlock();
        }
    }
    
    public void addFileToIndex(final String docId, final String text) throws IOException, URISyntaxException {
        final Lock writeLock = this.indexLock.writeLock();
        try {
            writeLock.lock();
            final IndexWriterConfig indexWriterConfig = new IndexWriterConfig((Analyzer)this.analyzer);
            final IndexWriter indexWriter = new IndexWriter(this.indexDirectory, indexWriterConfig);
            final Document document = new Document();
            document.add((IndexableField)new StringField("docId", docId, Field.Store.YES));
            document.add((IndexableField)new TextField("text", text, Field.Store.NO));
            SubjectIndex.LOGGER.info("Indexing document with text =\n[ %s \n]\n", new Object[] { text });
            indexWriter.addDocument((Iterable)document);
            indexWriter.close();
        }
        finally {
            writeLock.unlock();
        }
    }
    
    public List<Document> searchIndex(final String inField, final String queryString) throws IOException, ParseException {
        final Lock readLock = this.indexLock.readLock();
        try {
            readLock.lock();
            final Query query = new QueryParser(inField, (Analyzer)this.analyzer).parse(queryString);
            final IndexReader indexReader = (IndexReader)DirectoryReader.open(this.indexDirectory);
            final IndexSearcher searcher = new IndexSearcher(indexReader);
            final TopDocs topDocs = searcher.search(query, 50);
            final List<Document> documents = new ArrayList<Document>();
            for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }
            return documents;
        }
        finally {
            readLock.unlock();
        }
    }
    
    public void deleteDoc(final String docId) throws IOException {
        final Lock writeLock = this.indexLock.writeLock();
        try {
            writeLock.lock();
            final IndexWriterConfig indexWriterConfig = new IndexWriterConfig((Analyzer)this.analyzer);
            final IndexWriter indexWriter = new IndexWriter(this.indexDirectory, indexWriterConfig);
            indexWriter.deleteDocuments(new Term[] { new Term("docId", docId) });
            indexWriter.commit();
            indexWriter.close();
        }
        finally {
            writeLock.unlock();
        }
    }
    
    public void batchIndex(final IndexingBatch indexingBatch) throws IOException {
        final Lock writeLock = this.indexLock.writeLock();
        try {
            writeLock.lock();
            final IndexWriterConfig indexWriterConfig = new IndexWriterConfig((Analyzer)this.analyzer);
            final IndexWriter indexWriter = new IndexWriter(this.indexDirectory, indexWriterConfig);
            indexingBatch.getList().forEach(indexCommand -> {
                final String docId = indexCommand.getDocId();
                final String text = this.indexId.getType().equals("Question") ? indexCommand.getqContent() : indexCommand.getaContent();
                final Document document = new Document();
                document.add((IndexableField)new StringField("docId", docId, Field.Store.YES));
                document.add((IndexableField)new TextField("doc", docId, Field.Store.YES));
                document.add((IndexableField)new TextField("text", text, Field.Store.NO));
                try {
                    if (indexCommand.isUpdate()) {
                        SubjectIndex.LOGGER.info("%s => Indexing document UPDATE %s", new Object[] { this.indexId, docId });
                        indexWriter.updateDocument(new Term("docId", docId), (Iterable)document);
                    }
                    else {
                        SubjectIndex.LOGGER.info("%s => Indexing document ADD %s", new Object[] { this.indexId, docId });
                        indexWriter.addDocument((Iterable)document);
                    }
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            });
            indexWriter.commit();
            indexWriter.close();
        }
        finally {
            writeLock.unlock();
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)TextIndex.class);
    }
}
