// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.core.mapper.RowMapper;

class DocDB$3 implements RowMapper<Pair<String, Integer>> {
    public Pair<String, Integer> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String subject = rs.getString("subject");
        final int docCount = rs.getInt("docCount");
        return (Pair<String, Integer>)Pair.of((Object)subject, (Object)docCount);
    }
}