// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import com.yojito.minima.gson.GsonObject;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.oer.OERSegment;
import org.jdbi.v3.core.mapper.RowMapper;

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
