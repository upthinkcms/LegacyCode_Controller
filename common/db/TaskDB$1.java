// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.Task;
import org.jdbi.v3.core.mapper.RowMapper;

class TaskDB$1 implements RowMapper<Task> {
    public Task map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String name = rs.getString("name");
        final String shortname = rs.getString("shortname");
        final int id = rs.getInt("id");
        final boolean isInternal = rs.getBoolean("isInternal");
        final Task task = new Task(id, name, shortname, isInternal);
        return task;
    }
}