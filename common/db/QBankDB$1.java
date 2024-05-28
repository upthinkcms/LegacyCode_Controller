// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.util.QuestionConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.core.mapper.RowMapper;

class QBankDB$1 implements RowMapper<Pair<QuestionConfig, String>> {
    final /* synthetic */ String val$tenantKey;
    
    public Pair<QuestionConfig, String> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String docId = rs.getString("docId");
        final int qid = rs.getInt("qid");
        final int subjectId = rs.getInt("subjectId");
        final int topicId = rs.getInt("topicId");
        final int subTopicId = rs.getInt("subTopicId");
        final String answer = rs.getString("answer");
        return (Pair<QuestionConfig, String>)Pair.of((Object)new QuestionConfig(qid, subjectId, topicId, subTopicId, this.val$tenantKey, docId), (Object)answer);
    }
}