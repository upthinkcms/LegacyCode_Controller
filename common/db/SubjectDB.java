// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import com.upthinkexperts.common.util.CommonsUtil;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import org.jdbi.v3.core.mapper.RowMapper;
import com.upthinkexperts.common.domain.DBSubject;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.Handle;

public class SubjectDB
{
    public static int getSubjectId(final Handle dbHandle, final String subject) {
        return (int)((Query)dbHandle.createQuery("select id from subject where name = :subjectName").bind("subjectName", subject)).mapTo((Class)Integer.class).one();
    }
    
    public static List<String> listSubjects(final Handle dbHandle, final Integer[] subjectIds) {
        if (subjectIds == null || subjectIds.length == 0) {
            return new ArrayList<String>();
        }
        return ((Query)dbHandle.createQuery("select name from subject where id in (<subjectIds>)").bindList("subjectIds", (Iterable)Arrays.asList(subjectIds))).mapTo((Class)String.class).list();
    }
    
    public static List<DBSubject> listSubjectsAndId(final Handle dbHandle) {
        return dbHandle.createQuery("select * from subject").map((RowMapper)new RowMapper<DBSubject>() {
            public DBSubject map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String name = rs.getString("name");
                final String shortname = rs.getString("shortname");
                final int id = rs.getInt("id");
                final int parentId = CommonsUtil.getDBInt(rs, "parentid", -1);
                final DBSubject subject = new DBSubject(name, shortname, id, parentId);
                return subject;
            }
        }).list();
    }
}
