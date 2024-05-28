// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import org.jdbi.v3.core.statement.Query;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.jdbi.v3.core.mapper.RowMapper;
import com.upthinkexperts.common.domain.Task;
import java.util.List;
import org.jdbi.v3.core.Handle;

public class TaskDB
{
    private static final String LOAD_TASKS = "select * from TASK";
    private static final String LOAD_TASK = "select * from TASK where id = :taskId";
    
    public static List<Task> loadTasks(final Handle dbHandle) {
        return dbHandle.createQuery("select * from TASK").map((RowMapper)new RowMapper<Task>() {
            public Task map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String name = rs.getString("name");
                final String shortname = rs.getString("shortname");
                final int id = rs.getInt("id");
                final boolean isInternal = rs.getBoolean("isInternal");
                final Task task = new Task(id, name, shortname, isInternal);
                return task;
            }
        }).stream().collect(Collectors.toList());
    }
    
    public static Task loadTask(final Handle dbHandle, final int taskId) {
        return (Task)((Query)dbHandle.createQuery("select * from TASK where id = :taskId").bind("taskId", taskId)).map((RowMapper)new RowMapper<Task>() {
            public Task map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String name = rs.getString("name");
                final String shortname = rs.getString("shortname");
                final int id = rs.getInt("id");
                final boolean isInternal = rs.getBoolean("isInternal");
                final Task task = new Task(id, name, shortname, isInternal);
                return task;
            }
        }).one();
    }
}
