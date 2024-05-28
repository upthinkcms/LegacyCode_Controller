// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import com.yojito.minima.gson.GsonDto;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.DocResources;
import org.jdbi.v3.core.mapper.RowMapper;

class DocDB$4 implements RowMapper<DocResources> {
    public DocResources map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String resourcesJson = rs.getString("resources");
        final DocResources docResources = (DocResources)GsonDto.fromJson(resourcesJson, (Class)DocResources.class);
        return docResources;
    }
}