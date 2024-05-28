// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.UploadJob;
import org.jdbi.v3.core.mapper.RowMapper;

class UploadJobDB$3 implements RowMapper<UploadJob> {
    public UploadJob map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String uploadId = rs.getString("uploadId");
        final String fileName = rs.getString("fileName");
        final String status = rs.getString("status");
        final String info = rs.getString("info");
        final String url = rs.getString("url");
        final String parsingSpec = rs.getString("parsingSpec");
        final long timestamp = rs.getTimestamp("created_at").getTime();
        final String docFormatStr = rs.getString("docFormat");
        final String tenantKey = rs.getString("tenantKey");
        final UploadJob uploadJob = new UploadJob(uploadId, fileName, status, info, ParsingSpec.DocFormat.valueOf(docFormatStr), url, parsingSpec, timestamp, tenantKey);
        return uploadJob;
    }
}