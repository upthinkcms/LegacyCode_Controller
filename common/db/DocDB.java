// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import org.apache.commons.lang3.tuple.Pair;
import java.sql.SQLException;
import java.util.List;
import com.upthinkexperts.common.domain.DocResources;
import java.util.ArrayList;
import java.util.Arrays;
import com.yojito.minima.gson.GsonDto;
import com.upthinkexperts.common.domain.DocSection;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.jdbi.v3.core.mapper.RowMapper;
import java.util.Map;
import java.util.Optional;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;
import java.util.Collection;
import com.yojito.minima.gson.GsonObject;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import org.jdbi.v3.core.Handle;

public class DocDB
{
    private static final String DOCS_INSERT = "INSERT INTO DOCS(docId, subject, topic, subTopic, content, tags, resources, difficulty) VALUES(:docId, :subject, :topic, :subTopic, to_json(:content\\:\\:json), to_json(:tags\\:\\:json), to_json(:resources\\:\\:json), :difficulty)";
    private static final String DOCS_UPDATE = "UPDATE DOCS SET subject = :subject, topic = :topic, subTopic = :subTopic, content = to_json(:content\\:\\:json), tags = to_json(:tags\\:\\:json), resources = to_json(:resources\\:\\:json), difficulty = :difficulty  where docId = :docId";
    private static final String DOCS_UPDATE_RESOURCES = "UPDATE DOCS SET resources = to_json(:resources\\:\\:json) where docId = :docId";
    private static final String LOAD_DOC = "select * from DOCS where docId = :docId";
    private static final String LIST_DOC_JSON = "select docId, content from DOCS where docId in (<listOfDocIds>)";
    private static final String LOAD_DOC_ID = "select docId from DOCS where docId = :docId";
    private static final String LOAD_DOC_RESOURCES = "select resources from DOCS where docId = :docId";
    private static final String DELETE_DOCS = "delete from DOCS where docId = :docId";
    private static final String DELETE_ALL_DOCS = "delete from DOCS";
    private static final String COUNT_DOC_SUBJECT = "select subject, count(*) as docCount from docs group by subject";
    private static final String FAILED_JOB_QUERY = "select distinct uploads.uploadid, uploads.filename from file_uploads as uploads, file_upload_doc_files as docFiles\nwhere docFiles.uploadId = uploads.uploadid and docFiles.docid in (\n\tselect docId from docs where (resources -> 'equations3Key')::text = '\"\"'\n)";
    
    public static int insertDoc(final Handle dbHandle, final KnowledgeDoc doc) {
        final GsonObject tagsGson = new GsonObject();
        if (doc.getTags() != null) {
            tagsGson.put("tags", (Collection)doc.getTags());
        }
        return ((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("INSERT INTO DOCS(docId, subject, topic, subTopic, content, tags, resources, difficulty) VALUES(:docId, :subject, :topic, :subTopic, to_json(:content\\:\\:json), to_json(:tags\\:\\:json), to_json(:resources\\:\\:json), :difficulty)").bind("docId", doc.getDocId())).bind("subject", doc.getSubject())).bind("topic", doc.getTopic())).bind("subTopic", doc.getSubTopic())).bind("content", doc.getContent().toJson())).bind("tags", tagsGson.toJson())).bind("difficulty", doc.getDifficulty())).execute();
    }
    
    public static boolean upsertDoc(final Handle dbHandle, final KnowledgeDoc doc) {
        final Optional<Map<String, Object>> docRow = ((Query)dbHandle.createQuery("select docId from DOCS where docId = :docId").bind("docId", doc.getDocId())).mapToMap().findOne();
        final GsonObject tagsGson = new GsonObject();
        if (doc.getTags() != null) {
            tagsGson.put("tags", (Collection)doc.getTags());
        }
        if (docRow.isPresent()) {
            ((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("UPDATE DOCS SET subject = :subject, topic = :topic, subTopic = :subTopic, content = to_json(:content\\:\\:json), tags = to_json(:tags\\:\\:json), resources = to_json(:resources\\:\\:json), difficulty = :difficulty  where docId = :docId").bind("docId", doc.getDocId())).bind("subject", doc.getSubject())).bind("topic", doc.getTopic())).bind("subTopic", doc.getSubTopic())).bind("content", doc.getContent().toJson())).bind("tags", tagsGson.toJson())).bind("resources", doc.getResources().toJson())).bind("difficulty", doc.getDifficulty())).execute();
            return true;
        }
        ((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("INSERT INTO DOCS(docId, subject, topic, subTopic, content, tags, resources, difficulty) VALUES(:docId, :subject, :topic, :subTopic, to_json(:content\\:\\:json), to_json(:tags\\:\\:json), to_json(:resources\\:\\:json), :difficulty)").bind("docId", doc.getDocId())).bind("subject", doc.getSubject())).bind("topic", doc.getTopic())).bind("subTopic", doc.getSubTopic())).bind("content", doc.getContent().toJson())).bind("tags", tagsGson.toJson())).bind("resources", doc.getResources().toJson())).bind("difficulty", doc.getDifficulty())).execute();
        return false;
    }
    
    public static Optional<KnowledgeDoc> loadDoc(final Handle dbHandle, final String docId) {
        return ((Query)dbHandle.createQuery("select * from DOCS where docId = :docId").bind("docId", docId)).map((RowMapper)new RowMapper<KnowledgeDoc>() {
            public KnowledgeDoc map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String docId = rs.getString("docId");
                final String subject = rs.getString("subject");
                final String topic = rs.getString("topic");
                final String subTopic = rs.getString("subTopic");
                final String content = rs.getString("content");
                final String tagsJson = rs.getString("tags");
                final String difficulty = rs.getString("difficulty");
                final DocSection contentDoc = (DocSection)GsonDto.fromJson(content, (Class)DocSection.class);
                final GsonObject tagsGson = new GsonObject(tagsJson);
                final String[] tagsArray = tagsGson.optStringArray("tags");
                final List<String> tagsList = (tagsArray != null) ? Arrays.asList(tagsArray) : new ArrayList<String>();
                final String resourcesJson = rs.getString("resources");
                final DocResources docResources = (DocResources)GsonDto.fromJson(resourcesJson, (Class)DocResources.class);
                final KnowledgeDoc doc = new KnowledgeDoc(docId, contentDoc, true, subject, topic, subTopic, difficulty, tagsList, docResources);
                return doc;
            }
        }).findFirst();
    }
    
    public static List<Pair<String, String>> listContentJson(final Handle handle, final List<String> docIds) {
        return ((Query)handle.createQuery("select docId, content from DOCS where docId in (<listOfDocIds>)").bindList("listOfDocIds", (Iterable)docIds)).map((RowMapper)new RowMapper<Pair<String, String>>() {
            public Pair<String, String> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String content = rs.getString("content");
                final String docId = rs.getString("docId");
                return (Pair<String, String>)Pair.of((Object)docId, (Object)content);
            }
        }).list();
    }
    
    public static List<Pair<String, Integer>> getDocumentCountBySubject(final Handle handle) {
        return handle.createQuery("select subject, count(*) as docCount from docs group by subject").map((RowMapper)new RowMapper<Pair<String, Integer>>() {
            public Pair<String, Integer> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String subject = rs.getString("subject");
                final int docCount = rs.getInt("docCount");
                return (Pair<String, Integer>)Pair.of((Object)subject, (Object)docCount);
            }
        }).list();
    }
    
    public static void deleteUploadedDocs(final Handle dbHandle) {
        dbHandle.createUpdate("delete from DOCS").execute();
    }
    
    public static void deleteUploadedDoc(final Handle dbHandle, final String docId) {
        ((Update)dbHandle.createUpdate("delete from DOCS where docId = :docId").bind("docId", docId)).execute();
    }
    
    public static void updateResources(final Handle dbHandle, final String docId, final DocResources docResources) {
        ((Update)((Update)dbHandle.createUpdate("UPDATE DOCS SET resources = to_json(:resources\\:\\:json) where docId = :docId").bind("docId", docId)).bind("resources", docResources.toJson())).execute();
    }
    
    public static Optional<DocResources> loadResources(final Handle dbHandle, final String docId) {
        return ((Query)dbHandle.createQuery("select resources from DOCS where docId = :docId").bind("docId", docId)).map((RowMapper)new RowMapper<DocResources>() {
            public DocResources map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String resourcesJson = rs.getString("resources");
                final DocResources docResources = (DocResources)GsonDto.fromJson(resourcesJson, (Class)DocResources.class);
                return docResources;
            }
        }).findFirst();
    }
}
