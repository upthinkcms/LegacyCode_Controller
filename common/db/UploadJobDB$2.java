// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.core.mapper.RowMapper;

class UploadJobDB$2 implements RowMapper<Pair<String, String>> {
    public Pair<String, String> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String docFile = rs.getString("docFile");
        final String uploadId = rs.getString("uploadId");
        return (Pair<String, String>)Pair.of((Object)uploadId, (Object)docFile);
    }
}