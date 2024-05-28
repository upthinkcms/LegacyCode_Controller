// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import com.yojito.minima.gson.GsonObject;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import java.util.List;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.Query;
import java.util.Optional;
import org.jdbi.v3.core.statement.Update;
import com.upthinkexperts.common.domain.oer.OERSegment;
import org.jdbi.v3.core.Handle;

public class OERSegmentDB
{
    public static final String INSERT_SEGMENT = "insert into docsegments(parent_id, subject_id, topic_id, subtopic_id, type, path, tenantKey, segment, docId, label, attributes, jobid) \n values(:parentId, :subjectId, :topicId, :subTopicId, :type, :path, :tenantKey, :segment, :docId, :label, to_json(:attributes\\:\\:json), :jobId) \n ON CONFLICT(docId, path, tenantKey) DO UPDATE \n SET updated_at = CURRENT_TIMESTAMP, jobid = :jobId\n RETURNING id";
    public static final String LOAD_SUBJECT_SEGMENT = "select seg.* from subject sub, docsegments seg where seg.subject_id = sub.id and sub.name = :subject\nand seg.type = 'subject' and seg.tenantKey = :tenantKey\n";
    public static final String LOAD_CHILD_SEGMENT_FOR_TYPE = "select * from docsegments \nwhere type = 'segment' \nand segment = :segmentType \nand tenantKey = :tenantKey\nand parent_id = :parentId";
    public static final String LOAD_BOOK_SEGMENT = "select * from docsegments where parent_id = :subjectSegmentId and label = :book and type = 'book' and tenantKey = :tenantKey";
    private static final String LOAD_TOPIC_SEGMENT = "select seg.* from qbanktopics topic, docsegments seg where seg.topic_id = topic.id \nand topic.name = :topic \nand topic.subjectid = :subjectId\nand seg.type = 'topic' \nand seg.parent_id = :bookSegmentId \nand seg.tenantKey = :tenantKey \nand topic.tenantKey = :tenantKey";
    private static final String LOAD_SUBTOPIC_SEGMENT = "select seg.* from qbanksubtopics subtopic, docsegments seg where seg.subtopic_id = subtopic.id \nand subtopic.name = :subTopic \nand subtopic.subjectid = :subjectId \nand subtopic.topicid = :topicId\nand seg.type = 'subtopic' \nand seg.parent_id = :topicSegmentId \nand seg.tenantKey = :tenantKey \nand subtopic.tenantKey = :tenantKey";
    private static final String LIST_SEGMENT_TENANT = "select * from docsegments where tenantKey = :tenantKey";
    private static final String LOAD_DOCUMENT_SEGMENT = "select * from docsegments where label = :book and type = 'segment' and segment = 'document' and tenantKey = :tenantKey and path = '/'";
    
    public static OERSegment insertSegment(final Handle handle, final OERSegment segment) {
        final int id = (int)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)handle.createUpdate("insert into docsegments(parent_id, subject_id, topic_id, subtopic_id, type, path, tenantKey, segment, docId, label, attributes, jobid) \n values(:parentId, :subjectId, :topicId, :subTopicId, :type, :path, :tenantKey, :segment, :docId, :label, to_json(:attributes\\:\\:json), :jobId) \n ON CONFLICT(docId, path, tenantKey) DO UPDATE \n SET updated_at = CURRENT_TIMESTAMP, jobid = :jobId\n RETURNING id").bind("parentId", (segment.getParentId() != -1) ? Integer.valueOf(segment.getParentId()) : null)).bind("subjectId", (segment.getSubjectId() != -1) ? Integer.valueOf(segment.getSubjectId()) : null)).bind("topicId", (segment.getTopicId() != -1) ? Integer.valueOf(segment.getTopicId()) : null)).bind("subTopicId", (segment.getSubTopicId() != -1) ? Integer.valueOf(segment.getSubTopicId()) : null)).bind("type", (Object)segment.getType())).bind("path", segment.getPath())).bind("segment", segment.getSegment())).bind("docId", segment.getDocId())).bind("label", segment.getLabel())).bind("tenantKey", segment.getTenantKey())).bind("attributes", (segment.getAttributes() != null) ? segment.getAttributes().toJson() : null)).bind("jobId", segment.getJobId())).executeAndReturnGeneratedKeys(new String[] { "id" }).mapTo((Class)Integer.class).one();
        return segment.withId(id);
    }
    
    public static Optional<OERSegment> loadSubject(final Handle handle, final String subject, final String tenantKey) {
        return ((Query)((Query)handle.createQuery("select seg.* from subject sub, docsegments seg where seg.subject_id = sub.id and sub.name = :subject\nand seg.type = 'subject' and seg.tenantKey = :tenantKey\n").bind("subject", subject)).bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).findOne();
    }
    
    public static Optional<OERSegment> loadBook(final Handle handle, final String book, final int subjectSegmentId, final String tenantKey) {
        return ((Query)((Query)((Query)handle.createQuery("select * from docsegments where parent_id = :subjectSegmentId and label = :book and type = 'book' and tenantKey = :tenantKey").bind("book", book)).bind("subjectSegmentId", subjectSegmentId)).bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).findOne();
    }
    
    public static Optional<OERSegment> loadTopic(final Handle handle, final String topic, final int subjectId, final int bookSegmentId, final String tenantKey) {
        return ((Query)((Query)((Query)((Query)handle.createQuery("select seg.* from qbanktopics topic, docsegments seg where seg.topic_id = topic.id \nand topic.name = :topic \nand topic.subjectid = :subjectId\nand seg.type = 'topic' \nand seg.parent_id = :bookSegmentId \nand seg.tenantKey = :tenantKey \nand topic.tenantKey = :tenantKey").bind("topic", topic)).bind("subjectId", subjectId)).bind("bookSegmentId", bookSegmentId)).bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).findOne();
    }
    
    public static Optional<OERSegment> loadSubTopic(final Handle handle, final String subTopic, final int subjectId, final int topicId, final int topicSegmentId, final String tenantKey) {
        return ((Query)((Query)((Query)((Query)((Query)handle.createQuery("select seg.* from qbanksubtopics subtopic, docsegments seg where seg.subtopic_id = subtopic.id \nand subtopic.name = :subTopic \nand subtopic.subjectid = :subjectId \nand subtopic.topicid = :topicId\nand seg.type = 'subtopic' \nand seg.parent_id = :topicSegmentId \nand seg.tenantKey = :tenantKey \nand subtopic.tenantKey = :tenantKey").bind("subTopic", subTopic)).bind("subjectId", subjectId)).bind("topicId", topicId)).bind("topicSegmentId", topicSegmentId)).bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).findOne();
    }
    
    public static List<OERSegment> loadChildSegmentForType(final Handle handle, final int parentId, final String segmentType, final String tenantKey) {
        return ((Query)((Query)((Query)handle.createQuery("select * from docsegments \nwhere type = 'segment' \nand segment = :segmentType \nand tenantKey = :tenantKey\nand parent_id = :parentId").bind("segmentType", segmentType)).bind("parentId", parentId)).bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).list();
    }
    
    public static List<OERSegment> listSegments(final Handle handle, final String tenantKey) {
        return ((Query)handle.createQuery("select * from docsegments where tenantKey = :tenantKey").bind("tenantKey", tenantKey)).map((RowMapper)new OERSegmentMapper()).list();
    }
    
    public static class OERSegmentMapper implements RowMapper<OERSegment>
    {
        public OERSegment map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            final int id = rs.getInt("id");
            final int parentId = rs.getInt("parent_id");
            final int subjectId = rs.getInt("subject_id");
            final int topicId = rs.getInt("topic_id");
            final int subtopicId = rs.getInt("subtopic_id");
            final String type = rs.getString("type");
            final String path = rs.getString("path");
            final String tenantKey = rs.getString("tenantKey");
            final String segment = rs.getString("segment");
            final String docId = rs.getString("docId");
            final String label = rs.getString("label");
            final String attributesJson = rs.getString("attributes");
            GsonObject attributes = null;
            final String jobId = rs.getString("jobid");
            if (attributesJson != null) {
                attributes = new GsonObject(attributesJson);
            }
            return new OERSegment(id, parentId, subjectId, topicId, subtopicId, OERSegment.OERSegmentType.valueOf(type), segment, docId, path, tenantKey, label, attributes, null, null, jobId);
        }
    }
}
