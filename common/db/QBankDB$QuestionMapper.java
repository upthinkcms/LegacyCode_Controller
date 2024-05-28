// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.QBankDBQuestion;
import org.jdbi.v3.core.mapper.RowMapper;

public static class QuestionMapper implements RowMapper<QBankDBQuestion>
{
    public QBankDBQuestion map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final int qId = rs.getInt("qId");
        final String docId = rs.getString("docId");
        final String type = rs.getString("type");
        final int courseId = -1;
        final String specifiedQid = rs.getString("specifiedQid");
        final int level = rs.getInt("level");
        final String answer = rs.getString("answer");
        final String qDocKey = rs.getString("qDocKey");
        final String uploadId = rs.getString("uploadId");
        final String tenantKey = rs.getString("tenantKey");
        final int subjectId = rs.getInt("subjectId");
        final int topicId = rs.getInt("topicId");
        final int subtopicId = rs.getInt("subtopicId");
        return new QBankDBQuestion(qId, docId, courseId, specifiedQid, subjectId, topicId, subtopicId, level, type, answer, qDocKey, uploadId, tenantKey);
    }
}
