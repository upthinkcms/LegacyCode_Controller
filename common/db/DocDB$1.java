// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import java.sql.SQLException;
import java.util.List;
import com.upthinkexperts.common.domain.DocResources;
import java.util.ArrayList;
import java.util.Arrays;
import com.yojito.minima.gson.GsonObject;
import com.yojito.minima.gson.GsonDto;
import com.upthinkexperts.common.domain.DocSection;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import org.jdbi.v3.core.mapper.RowMapper;

class DocDB$1 implements RowMapper<KnowledgeDoc> {
    public KnowledgeDoc map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String docId = rs.getString("docId");
        final String subject = rs.getString("subject");
        final String topic = rs.getString("topic");
        final String subTopic = rs.getString("subTopic");
        final String content = rs.getString("content");
        final String tagsJson = rs.getString("tags");
        final String difficulty = rs.getString("difficulty");
        final DocSection contentDoc = (DocSection)GsonDto.fromJson(content, (Class)DocSection.class);
        final GsonObject tagsGson = new GsonObject(tagsJson);
        final String[] tagsArray = tagsGson.optStringArray("tags");
        final List<String> tagsList = (tagsArray != null) ? Arrays.asList(tagsArray) : new ArrayList<String>();
        final String resourcesJson = rs.getString("resources");
        final DocResources docResources = (DocResources)GsonDto.fromJson(resourcesJson, (Class)DocResources.class);
        final KnowledgeDoc doc = new KnowledgeDoc(docId, contentDoc, true, subject, topic, subTopic, difficulty, tagsList, docResources);
        return doc;
    }
}