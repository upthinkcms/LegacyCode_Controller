// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.JobFile;
import org.jdbi.v3.core.mapper.RowMapper;

class UploadJobDB$4 implements RowMapper<JobFile> {
    public JobFile map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String uploadId = rs.getString("uploadId");
        final String docId = rs.getString("docId");
        final String docFile = rs.getString("docFile");
        final boolean isQuestion = rs.getBoolean("isQuestion");
        final boolean parsed = rs.getBoolean("parsed");
        final boolean indexed = rs.getBoolean("indexed");
        final boolean uploaded = rs.getBoolean("uploaded");
        final String error = rs.getString("error");
        final String errorCodeString = rs.getString("errorCode");
        final String tenantKey = rs.getString("tenantKey");
        final JobFile.JobFileErrorCodes errorCode = (errorCodeString != null) ? JobFile.JobFileErrorCodes.valueOf(errorCodeString) : null;
        final JobFile jobFile = new JobFile(docId, uploadId, docFile, isQuestion, parsed, indexed, uploaded, error, errorCode, tenantKey);
        return jobFile;
    }
}