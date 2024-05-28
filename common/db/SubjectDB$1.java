// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import com.upthinkexperts.common.util.CommonsUtil;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.DBSubject;
import org.jdbi.v3.core.mapper.RowMapper;

class SubjectDB$1 implements RowMapper<DBSubject> {
    public DBSubject map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String name = rs.getString("name");
        final String shortname = rs.getString("shortname");
        final int id = rs.getInt("id");
        final int parentId = CommonsUtil.getDBInt(rs, "parentid", -1);
        final DBSubject subject = new DBSubject(name, shortname, id, parentId);
        return subject;
    }
}