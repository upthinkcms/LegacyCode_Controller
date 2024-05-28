// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload.oer;

import com.upthinkexperts.common.domain.oer.OERLearningObjective;
import com.upthinkexperts.common.domain.oer.OERSectionExerciseSet;
import com.upthinkexperts.common.domain.oer.OERContent;
import java.util.Map;
import java.util.Optional;
import java.sql.Timestamp;
import com.yojito.minima.gson.GsonObject;
import com.upthinkexperts.common.db.SubjectDB;
import com.upthinkexperts.common.db.OERSegmentDB;
import java.util.ArrayList;
import com.upthinkexperts.common.domain.oer.OERSegment;
import java.util.List;
import org.jdbi.v3.core.Handle;
import com.upthinkexperts.common.domain.oer.OERDocument;
import com.yojito.minima.logging.MinimaLogger;

public class OERSegments
{
    private static final MinimaLogger LOGGER;
    
    public static List<OERSegment> createAndReturnSegments(final OERDocument document, final String tenantKey, final Handle handle, final String jobId) {
        final List<OERSegment> segmentList = new ArrayList<OERSegment>();
        final String subject = document.getSubject();
        Optional<OERSegment> subjectSegment = OERSegmentDB.loadSubject(handle, subject, tenantKey);
        if (subjectSegment.isEmpty()) {
            final int subjectId = SubjectDB.getSubjectId(handle, subject);
            OERSegments.LOGGER.debug("Adding new subject segement - %s id(%d) -> %s", new Object[] { subject, subjectId, tenantKey });
            subjectSegment = Optional.of(OERSegmentDB.insertSegment(handle, new OERSegment(-1, -1, subjectId, -1, -1, OERSegment.OERSegmentType.subject, (String)null, "_$_", "/subject/" + subject, tenantKey, subject, (GsonObject)null, (Timestamp)null, (Timestamp)null, jobId)));
        }
        Optional<OERSegment> bookSegment = OERSegmentDB.loadBook(handle, document.getBook(), subjectSegment.get().getId(), tenantKey);
        if (bookSegment.isEmpty()) {
            OERSegments.LOGGER.debug("Adding new book segement - %s parent(%d) subject(%d) -> %s", new Object[] { document.getBook(), subjectSegment.get().getId(), subjectSegment.get().getSubjectId(), tenantKey });
            bookSegment = Optional.of(OERSegmentDB.insertSegment(handle, new OERSegment(-1, subjectSegment.get().getId(), subjectSegment.get().getSubjectId(), -1, -1, OERSegment.OERSegmentType.book, (String)null, "_$_", "/book/" + document.getBook(), tenantKey, document.getBook(), (GsonObject)null, (Timestamp)null, (Timestamp)null, jobId)));
        }
        Optional<OERSegment> topicSegment = OERSegmentDB.loadTopic(handle, document.getTopic(), subjectSegment.get().getSubjectId(), bookSegment.get().getId(), tenantKey);
        if (topicSegment.isEmpty()) {
            OERSegments.LOGGER.debug("Adding new topic segement - %s parent(%d) subject(%d) book(%d)-> %s", new Object[] { document.getTopic(), bookSegment.get().getId(), subjectSegment.get().getSubjectId(), bookSegment.get().getId(), tenantKey });
            topicSegment = Optional.of(OERSegmentDB.insertSegment(handle, new OERSegment(-1, bookSegment.get().getId(), subjectSegment.get().getSubjectId(), -1, -1, OERSegment.OERSegmentType.topic, (String)null, "_$_", "/topic/" + document.getTopic(), tenantKey, document.getTopic(), (GsonObject)null, (Timestamp)null, (Timestamp)null, jobId)));
        }
        Optional<OERSegment> subTopicSegment = OERSegmentDB.loadSubTopic(handle, document.getSubTopic(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), topicSegment.get().getId(), tenantKey);
        if (subTopicSegment.isEmpty()) {
            OERSegments.LOGGER.debug("Adding new subtopic segment - %s parent(%d) subject(%d) book(%d) topic(%d)-> %s", new Object[] { document.getSubTopic(), topicSegment.get().getId(), subjectSegment.get().getSubjectId(), bookSegment.get().getId(), topicSegment.get().getId(), tenantKey });
            subTopicSegment = Optional.of(OERSegmentDB.insertSegment(handle, new OERSegment(-1, topicSegment.get().getId(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), -1, OERSegment.OERSegmentType.subtopic, (String)null, "_$_", "/subtopic/" + document.getSubTopic(), tenantKey, document.getSubTopic(), (GsonObject)null, (Timestamp)null, (Timestamp)null, jobId)));
        }
        OERSegment documentSegment = new OERSegment(-1, subTopicSegment.get().getId(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), subTopicSegment.get().getSubTopicId(), OERSegment.OERSegmentType.segment, "document", document.getDocId(), "/", tenantKey, document.getSubTopic(), null, null, null, jobId);
        OERSegments.LOGGER.debug("Adding new document segment - %s parent(%d) subject(%d) book(%d) topic(%d)-> %s", new Object[] { document.getDocId(), topicSegment.get().getId(), subjectSegment.get().getSubjectId(), bookSegment.get().getId(), topicSegment.get().getId(), tenantKey });
        documentSegment = OERSegmentDB.insertSegment(handle, documentSegment);
        segmentList.add(documentSegment);
        final OERLearningObjective learningObjective = document.getLearningObjective();
        List<OERSegment> learningObjectSegments = null;
        if (learningObjective != null) {
            learningObjectSegments = OERSegmentDB.loadChildSegmentForType(handle, documentSegment.getId(), "learningobjective", tenantKey);
        }
        if (learningObjectSegments != null || learningObjectSegments.size() == 0) {
            final String jsonPath = "$.learningObjective";
            OERSegment learningObjectSegment = new OERSegment(-1, documentSegment.getId(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), subTopicSegment.get().getSubTopicId(), OERSegment.OERSegmentType.segment, "learningobjective", document.getDocId(), jsonPath, tenantKey, null, new GsonObject((Map)learningObjective.getAttributes()), null, null, jobId);
            OERSegments.LOGGER.debug("Adding new learning objective segment - parent(%d) -> %s", new Object[] { documentSegment.getId(), tenantKey });
            learningObjectSegment = OERSegmentDB.insertSegment(handle, learningObjectSegment);
            learningObjectSegments.add(learningObjectSegment);
        }
        segmentList.add(learningObjectSegments.get(0));
        for (int i = 0; i < document.getContentList().size(); ++i) {
            final OERContent content = document.getContentList().get(i);
            final String jsonPath2 = String.format("$.contentList[%d]", i);
            String label = null;
            if (content.getAttributes() == null || !content.getAttributes().containsKey("label")) {
                throw new RuntimeException("Label tag is missing for Content index=%d" + i);
            }
            label = content.getAttributes().get("label");
            OERSegments.LOGGER.debug("Adding new content segment - parent(%d) [%d] label = %s -> %s", new Object[] { documentSegment.getId(), i, label, tenantKey });
            OERSegment contentSegment = new OERSegment(-1, documentSegment.getId(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), subTopicSegment.get().getSubTopicId(), OERSegment.OERSegmentType.segment, "content", document.getDocId(), jsonPath2, tenantKey, label, new GsonObject((Map)content.getAttributes()), null, null, jobId);
            contentSegment = OERSegmentDB.insertSegment(handle, contentSegment);
            OERSegments.LOGGER.debug("Added new content segment - %s", new Object[] { contentSegment });
            segmentList.add(contentSegment);
        }
        for (int i = 0; i < document.getExerciseSetList().size(); ++i) {
            final OERSectionExerciseSet set = document.getExerciseSetList().get(i);
            String label2 = "Exercise Set";
            if (set.getAttributes() != null && set.getAttributes().containsKey("label")) {
                label2 = set.getAttributes().get("label");
                OERSegments.LOGGER.debug("Adding new Exercise Set segment - parent(%d) [%d] label = %s -> %s", new Object[] { documentSegment.getId(), i, label2, tenantKey });
            }
            final String jsonPath3 = String.format("$.exerciseSetList[%d]", i);
            OERSegment contentSegment = new OERSegment(-1, documentSegment.getId(), subjectSegment.get().getSubjectId(), topicSegment.get().getTopicId(), subTopicSegment.get().getSubTopicId(), OERSegment.OERSegmentType.segment, "exerciseset", document.getDocId(), jsonPath3, tenantKey, label2, new GsonObject((Map)set.getAttributes()), null, null, jobId);
            contentSegment = OERSegmentDB.insertSegment(handle, contentSegment);
            OERSegments.LOGGER.debug("Added new exerciseSet segment - %s", new Object[] { contentSegment });
            segmentList.add(contentSegment);
        }
        return segmentList;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERSegments.class);
    }
}
