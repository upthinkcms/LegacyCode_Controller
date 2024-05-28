// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.db;

import com.upthinkexperts.common.util.QBankUtil;
import java.util.Iterator;
import java.util.Collection;
import com.upthinkexperts.common.domain.DBSubject;
import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;
import com.upthinkexperts.common.util.QuestionConfig;
import org.jdbi.v3.core.mapper.RowMapper;
import com.upthinkexperts.common.domain.QBankDBQuestion;
import com.upthinkexperts.common.domain.QBankSubTopic;
import com.upthinkexperts.common.domain.QBankTopic;
import org.jdbi.v3.core.statement.Update;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.jdbi.v3.core.statement.Query;
import java.util.HashMap;
import com.upthinkexperts.common.domain.QBankCourse;
import java.util.List;
import org.jdbi.v3.core.Handle;

public class QBankDB
{
    private static final String QBANK_COURSE_LIST = "select * from qbankcourse where tenantKey = :tenantKey";
    private static final String QBANK_COURSE_SUBJECTS_LIST = "select * from qbankcourse_subjects where tenantKey = :tenantKey";
    private static final String QBANK_COURSE_LIST_NAME = "select * from qbankcourse where name = :name and tenantKey = :tenantKey";
    private static final String QBANK_COURSE_LIST_SUBJECTIDS = "select subject_id from qbankcourse_subjects where course_id = :id and tenantKey = :tenantKey";
    private static final String QBANK_COURSE_INSERT = "insert into qbankcourse(name, tenantKey) values(:name, :tenantKey)";
    private static final String QBANK_COURSE_SUBJECT_INSERT = "insert into qbankcourse_subjects(course_id, subject_id, tenantKey) values(:courseId, :subjectId, :tenantKey)";
    private static final String QBANK_TOPIC_LIST = "select * from qbanktopics where id in (    select distinct topicid from questions where    subjectid = :subjectId and    docid in (<docIds>) and    tenantkey = :tenantKey and    deleted = false)";
    private static final String QBANK_TOPIC_LIST_WITHOUT_DOC_IDS = "select * from qbanktopics where subjectId = :subjectId and tenantKey = :tenantKey";
    private static final String GET_QBANK_TOPIC_BY_NAME = "select * from qbanktopics where subjectId = :subjectId and name = :name and tenantKey = :tenantKey";
    private static final String QBANK_SUBTOPIC_LIST = "select * from qbanksubtopics where id in (    select distinct subtopicid from questions where    subjectid = :subjectId and    topicid in (<topicIds>) and    docid in (<docIds>) and    tenantkey = :tenantKey and    deleted = false)";
    private static final String QBANK_SUBTOPIC_LIST_WITHOUT_DOC_IDS = "select * from qbanksubtopics where subjectId = :subjectId and topicId in (<topicIds>) and id in (select distinct subTopicId from questions where tenantKey = :tenantKey and deleted = false)";
    private static final String GET_QBANK_SUBTOPIC_BY_NAME = "select * from qbanksubtopics where subjectId = :subjectId and topicId = :topicId and name = :name";
    private static final String LIST_DOCIDS = "select distinct docid from questions where subjectId = :subjectId and tenantKey = :tenantKey and deleted = false";
    private static final String GET_Q_COUNT_SUBTOPIC = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and level = :level and tenantKey = :tenantKey and deleted = false";
    private static final String GET_Q_COUNT_SUBTOPIC_NO_LEVEL = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and tenantKey = :tenantKey and deleted = false";
    private static final String GET_Q_COUNT_SUBTOPIC_DOCS = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and level = :level and tenantKey = :tenantKey and deleted = false";
    private static final String GET_Q_COUNT_SUBTOPIC_DOCS_NO_LEVEL = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and tenantKey = :tenantKey and deleted = false";
    private static final String INSERT_TOPIC = "insert into qbanktopics(name, subjectId, tenantKey) values(:name, :subjectId, :tenantKey)";
    private static final String INSERT_SUBTOPIC = "insert into qbanksubtopics(name, topicId, subjectId, tenantKey) values(:name, :topicId, :subjectId, :tenantKey)";
    private static final String INSERT_QUESTION = "insert into questions(qid, docId, courseId, specifiedQid, subjectId, topicId, subTopicId, level, type, qdocKey, answer, uploadid, tenantKey, deleted) values(:qId, :docId, :courseId, :specifiedQid, :subjectId, :topicId, :subTopicId, :level, :type, :qDocKey, :answer, :uploadId, :tenantKey, false)";
    private static final String UPDATE_QUESTION = "update questions set courseId = :courseId, specifiedQid = :specifiedQid, topicId = :topicId, subTopicId = :subTopicId, level = :level, type = :type, answer = :answer, uploadid = :uploadId, deleted = false where qId = :qId and docId = :docId and tenantKey = :tenantKey";
    private static final String GET_QUESTION = "SELECT * FROM questions WHERE qid = :qId AND docid = :docId AND subjectid = :subjectId AND topicid = :topicId AND subtopicid = :subTopicId AND tenantkey = :tenantKey AND deleted = false";
    private static final String LOAD_QUESTION_WITHOUT_DELETION_CHECK = "SELECT * FROM questions WHERE qid = :qId AND docid = :docId AND subjectid = :subjectId AND topicid = :topicId AND subtopicid = :subTopicId AND tenantkey = :tenantKey";
    private static final String GET_LEVELS = "select distinct level from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and tenantKey = :tenantKey and deleted = false";
    private static final String GET_LEVELS_DOCIDS = "select distinct level from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and tenantKey = :tenantKey and deleted = false";
    private static final String GET_ANSWERS_FOR_QUESTIONS = "select t.docId, t.qid, t.answer, t.subjectId, t.topicId, t.subTopicId from ( select docId, qid, answer, subjectId, topicId, subTopicId from questions where tenantKey = :tenantKey and deleted = false) as t where (t.docId,t.qid,t.subjectId,t.topicId,t.subTopicId) in (<questionBankIds>)";
    private static final String SOFT_DELETE_QUESTIONS = "update questions set deleted = true where uploadid = :uploadId and qid = :questionId";
    private static final String GET_QUESTIONS_FROM_ULOAD_ID = "select * from questions where uploadid : uploadId and deleted = false";
    private static final String GET_SUBJECTS_FOR_FILE_UPLOAD = "select distinct s.id, s.name from questions q, subject s where q.subjectid = s.id and q.uploadid = :uploadId and q.deleted = false";
    private static final String GET_TOPICS_FOR_FILE_UPLOAD_FROM_SUBJECT_ID = "select distinct qbt.id, qbt.name from questions q, subject s, qbanktopics qbt where q.topicid = qbt.id and q.subjectid = :subjectId and q.uploadid = :uploadId and q.deleted = false;";
    private static final String GET_SUB_TOPICS_FOR_FILE_UPLOAD_FROM_TOPIC_ID = "select distinct qbst.id, qbst.name from questions q, qbanktopics qbt, qbanksubtopics qbst where q.topicid = :topicId and q.subtopicId = qbst.id and q.uploadid = :uploadId and q.deleted = false;";
    private static final String GET_QUESTIONS_FOR_SUB_TOPIC_FROM_UPLOADID = "select * from questions where uploadid = :uploadId and subjectid = :subjectId and topicid = :topicId and subtopicid = :subTopicId and deleted = false";
    private static final String GET_QUESTIONS_FOR_PART = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
    private static final String GET_QUESTIONS_FOR_PART_WITH_LEVEL = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
    private static final String GET_QUESTIONS_FOR_PART_WITH_DOCIDS = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
    private static final String GET_QUESTIONS_FOR_PART_WITH_DOCIDS_AND_LEVEL = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
    private static final String CHANGE_QUESTION_FOR_PART = "SELECT * FROM questions WHERE subjectId = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantKey = :tenantKey AND deleted = false AND qid NOT IN (<existingQIds>) ORDER BY RANDOM() LIMIT 1";
    private static final String CHANGE_QUESTION_FOR_PART_WITH_DOCIDS = "SELECT * FROM questions WHERE subjectId = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantKey = :tenantKey AND docid IN (<docIds>) AND deleted = false AND qid NOT IN (<existingQIds>) ORDER BY RANDOM() LIMIT 1";
    private static final String SOFT_DELETE_QUESTIONS_IN_FILE_UPLOAD = "update questions set deleted = true where uploadid = :uploadId";
    
    public static List<QBankCourse> listCourses(final Handle dbHandle, final String tenantKey) {
        final Map<Integer, String> courseNameMap = new HashMap<Integer, String>();
        final Map<Integer, List<Integer>> courseSubjectsMap = new HashMap<Integer, List<Integer>>();
        ((Query)dbHandle.createQuery("select * from qbankcourse where tenantKey = :tenantKey").bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            return Pair.of((Object)id, (Object)name);
        }).forEach(pair -> courseNameMap.put(pair.getLeft(), pair.getRight()));
        ((Query)dbHandle.createQuery("select * from qbankcourse_subjects where tenantKey = :tenantKey").bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final int courseId = rs.getInt("course_id");
            final int subjectId = rs.getInt("subject_id");
            return Pair.of((Object)courseId, (Object)subjectId);
        }).forEach(pair -> {
            List<Integer> subjectIds = courseSubjectsMap.get(pair.getLeft());
            if (subjectIds == null) {
                subjectIds = new ArrayList<Integer>();
            }
            subjectIds.add((Integer)pair.getRight());
            courseSubjectsMap.put(pair.getLeft(), subjectIds);
            return;
        });
        return courseNameMap.entrySet().stream().map(entry -> {
            final List<Integer> subjectIds2 = courseSubjectsMap.get(entry.getKey());
            return new QBankCourse(entry.getKey(), entry.getValue(), subjectIds2, tenantKey);
        }).collect((Collector<? super Object, ?, List<QBankCourse>>)Collectors.toList());
    }
    
    public static Optional<QBankCourse> listCoursesByName(final Handle dbHandle, final String name, final String tenantKey) {
        final Optional<Integer> courseIdOptional = ((Query)((Query)dbHandle.createQuery("select * from qbankcourse where name = :name and tenantKey = :tenantKey").bind("tenantKey", tenantKey)).bind("name", name)).map((rs, ctx) -> rs.getInt("id")).findFirst();
        if (courseIdOptional.isPresent()) {
            final int courseId = courseIdOptional.get();
            final List<Integer> subjectIds = ((Query)((Query)dbHandle.createQuery("select subject_id from qbankcourse_subjects where course_id = :id and tenantKey = :tenantKey").bind("id", courseId)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
                final int id = rs.getInt("subject_id");
                return id;
            }).list();
            return Optional.of(new QBankCourse(courseId, name, subjectIds, tenantKey));
        }
        return Optional.empty();
    }
    
    public static int insertCourse(final Handle dbHandle, final String course, final int subjectId, final String tenantKey) {
        final int courseId = (int)((Update)((Update)dbHandle.createUpdate("insert into qbankcourse(name, tenantKey) values(:name, :tenantKey)").bind("name", course)).bind("tenantKey", tenantKey)).executeAndReturnGeneratedKeys(new String[] { "id" }).mapTo((Class)Integer.TYPE).first();
        ((Update)((Update)((Update)dbHandle.createUpdate("insert into qbankcourse_subjects(course_id, subject_id, tenantKey) values(:courseId, :subjectId, :tenantKey)").bind("courseId", courseId)).bind("subjectId", subjectId)).bind("tenantKey", tenantKey)).execute();
        return courseId;
    }
    
    public static void linkSubjectWithCourse(final Handle dbHandle, final int subjectId, final int courseId, final String tenantKey) {
        ((Update)((Update)((Update)dbHandle.createUpdate("insert into qbankcourse_subjects(course_id, subject_id, tenantKey) values(:courseId, :subjectId, :tenantKey)").bind("courseId", courseId)).bind("subjectId", subjectId)).bind("tenantKey", tenantKey)).execute();
    }
    
    public static List<QBankTopic> listTopicsForSubject(final Handle dbHandle, final int subjectId, final List<String> docIds, final String tenantKey) {
        return ((Query)((Query)((Query)dbHandle.createQuery("select * from qbanktopics where id in (    select distinct topicid from questions where    subjectid = :subjectId and    docid in (<docIds>) and    tenantkey = :tenantKey and    deleted = false)").bind("subjectId", subjectId)).bindList("docIds", (Iterable)docIds)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String name = rs.getString("name");
            final int id = rs.getInt("id");
            return new QBankTopic(id, name, subjectId, tenantKey);
        }).list();
    }
    
    public static List<QBankTopic> listTopicsForSubject(final Handle dbHandle, final int subjectId, final String tenantKey) {
        return ((Query)((Query)dbHandle.createQuery("select * from qbanktopics where subjectId = :subjectId and tenantKey = :tenantKey").bind("subjectId", subjectId)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String name = rs.getString("name");
            final int id = rs.getInt("id");
            return new QBankTopic(id, name, subjectId, tenantKey);
        }).list();
    }
    
    public static Optional<QBankTopic> getTopicByName(final Handle dbHandle, final int subjectId, final String name, final String tenantKey) {
        return ((Query)((Query)((Query)dbHandle.createQuery("select * from qbanktopics where subjectId = :subjectId and name = :name and tenantKey = :tenantKey").bind("subjectId", subjectId)).bind("name", name)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String name2 = rs.getString("name");
            final int id = rs.getInt("id");
            return new QBankTopic(id, name2, subjectId, tenantKey);
        }).findFirst();
    }
    
    public static int insertTopic(final Handle dbHandle, final String name, final int subjectId, final String tenantKey) {
        return (int)((Update)((Update)((Update)dbHandle.createUpdate("insert into qbanktopics(name, subjectId, tenantKey) values(:name, :subjectId, :tenantKey)").bind("name", name)).bind("subjectId", subjectId)).bind("tenantKey", tenantKey)).executeAndReturnGeneratedKeys(new String[] { "id" }).mapTo((Class)Integer.TYPE).first();
    }
    
    public static int insertSubTopic(final Handle dbHandle, final String name, final int subjectId, final int topicId, final String tenantKey) {
        return (int)((Update)((Update)((Update)((Update)dbHandle.createUpdate("insert into qbanksubtopics(name, topicId, subjectId, tenantKey) values(:name, :topicId, :subjectId, :tenantKey)").bind("name", name)).bind("subjectId", subjectId)).bind("topicId", topicId)).bind("tenantKey", tenantKey)).executeAndReturnGeneratedKeys(new String[] { "id" }).mapTo((Class)Integer.TYPE).first();
    }
    
    public static List<QBankSubTopic> listSubTopicsForSubjectAndTopics(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final List<String> docIds, final String tenantKey) {
        return ((Query)((Query)((Query)((Query)dbHandle.createQuery("select * from qbanksubtopics where id in (    select distinct subtopicid from questions where    subjectid = :subjectId and    topicid in (<topicIds>) and    docid in (<docIds>) and    tenantkey = :tenantKey and    deleted = false)").bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("docIds", (Iterable)docIds)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String name = rs.getString("name");
            final int id = rs.getInt("id");
            final int topicId = rs.getInt("topicid");
            return new QBankSubTopic(id, name, subjectId, topicId, tenantKey);
        }).list();
    }
    
    public static List<QBankSubTopic> listSubTopicsForSubjectAndTopics(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final String tenantKey) {
        return ((Query)((Query)((Query)dbHandle.createQuery("select * from qbanksubtopics where subjectId = :subjectId and topicId in (<topicIds>) and id in (select distinct subTopicId from questions where tenantKey = :tenantKey and deleted = false)").bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final String name = rs.getString("name");
            final int id = rs.getInt("id");
            final int topicId = rs.getInt("topicid");
            return new QBankSubTopic(id, name, subjectId, topicId, tenantKey);
        }).list();
    }
    
    public static Optional<QBankSubTopic> getSubTopicForSubjectAndTopic(final Handle dbHandle, final int subjectId, final int topicId, final String name, final String tenantKey) {
        return ((Query)((Query)((Query)dbHandle.createQuery("select * from qbanksubtopics where subjectId = :subjectId and topicId = :topicId and name = :name").bind("subjectId", subjectId)).bind("topicId", topicId)).bind("name", name)).map((rs, ctx) -> {
            final String name2 = rs.getString("name");
            final int id = rs.getInt("id");
            return new QBankSubTopic(id, name2, subjectId, topicId, tenantKey);
        }).findFirst();
    }
    
    public static List<String> listDocIds(final Handle dbHandle, final int subjectId, final String tenantKey) {
        return ((Query)((Query)dbHandle.createQuery("select distinct docid from questions where subjectId = :subjectId and tenantKey = :tenantKey and deleted = false").bind("subjectId", subjectId)).bind("tenantKey", tenantKey)).mapTo((Class)String.class).list();
    }
    
    public static int getNumQuestionsForSubTopic(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final int level, final String tenantKey) {
        String query;
        if (level == -1) {
            query = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and tenantKey = :tenantKey and deleted = false";
        }
        else {
            query = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and level = :level and tenantKey = :tenantKey and deleted = false";
        }
        return (int)((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery(query).bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bind("level", level)).bind("tenantKey", tenantKey)).mapTo((Class)Integer.TYPE).one();
    }
    
    public static int getNumQuestionsForSubTopicDocs(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final List<String> docIds, final int level, final String tenantKey) {
        String query;
        if (level == -1) {
            query = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and tenantKey = :tenantKey and deleted = false";
        }
        else {
            query = "select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and level = :level and tenantKey = :tenantKey and deleted = false";
        }
        return (int)((Query)((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery(query).bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bindList("docIds", (Iterable)docIds)).bind("level", level)).bind("tenantKey", tenantKey)).mapTo((Class)Integer.TYPE).one();
    }
    
    public static int insertQuestion(final Handle dbHandle, final QBankDBQuestion question) {
        return ((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("insert into questions(qid, docId, courseId, specifiedQid, subjectId, topicId, subTopicId, level, type, qdocKey, answer, uploadid, tenantKey, deleted) values(:qId, :docId, :courseId, :specifiedQid, :subjectId, :topicId, :subTopicId, :level, :type, :qDocKey, :answer, :uploadId, :tenantKey, false)").bind("qId", question.getId())).bind("docId", question.getDocId())).bind("courseId", (Integer)null)).bind("specifiedQid", question.getSpecifiedQid())).bind("subjectId", question.getSubjectId())).bind("topicId", question.getTopicId())).bind("subTopicId", (question.getSubTopicId() != -1) ? Integer.valueOf(question.getSubTopicId()) : null)).bind("level", question.getLevel())).bind("type", question.getType())).bind("qDocKey", question.getqDocKey())).bind("uploadId", question.getUploadId())).bind("answer", question.getAnswer())).bind("tenantKey", question.getTenantKey())).execute();
    }
    
    public static int updateQuestion(final Handle dbHandle, final QBankDBQuestion question) {
        return ((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)((Update)dbHandle.createUpdate("update questions set courseId = :courseId, specifiedQid = :specifiedQid, topicId = :topicId, subTopicId = :subTopicId, level = :level, type = :type, answer = :answer, uploadid = :uploadId, deleted = false where qId = :qId and docId = :docId and tenantKey = :tenantKey").bind("qId", question.getId())).bind("docId", question.getDocId())).bind("courseId", (Integer)null)).bind("specifiedQid", question.getSpecifiedQid())).bind("subjectId", question.getSubjectId())).bind("topicId", question.getTopicId())).bind("subTopicId", (question.getSubTopicId() != -1) ? Integer.valueOf(question.getSubTopicId()) : null)).bind("level", question.getLevel())).bind("type", question.getType())).bind("qDocKey", question.getqDocKey())).bind("uploadId", question.getUploadId())).bind("answer", question.getAnswer())).bind("tenantKey", question.getTenantKey())).execute();
    }
    
    public static Optional<QBankDBQuestion> loadQuestion(final Handle dbHandle, final int qId, final String docId, final String tenantKey, final int subjectId, final int topicId, final int subTopicId) {
        return ((Query)((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery("SELECT * FROM questions WHERE qid = :qId AND docid = :docId AND subjectid = :subjectId AND topicid = :topicId AND subtopicid = :subTopicId AND tenantkey = :tenantKey AND deleted = false").bind("qId", qId)).bind("docId", docId)).bind("tenantKey", tenantKey)).bind("subjectId", subjectId)).bind("topicId", topicId)).bind("subTopicId", subTopicId)).map((RowMapper)new QuestionMapper()).findFirst();
    }
    
    public static Optional<QBankDBQuestion> loadQuestionWithoutDeletionCheck(final Handle dbHandle, final int qId, final String docId, final String tenantKey, final int subjectId, final int topicId, final int subTopicId) {
        return ((Query)((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery("SELECT * FROM questions WHERE qid = :qId AND docid = :docId AND subjectid = :subjectId AND topicid = :topicId AND subtopicid = :subTopicId AND tenantkey = :tenantKey").bind("qId", qId)).bind("docId", docId)).bind("tenantKey", tenantKey)).bind("subjectId", subjectId)).bind("topicId", topicId)).bind("subTopicId", subTopicId)).map((RowMapper)new QuestionMapper()).findFirst();
    }
    
    public static List<String> getLevels(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final String tenantKey) {
        return ((Query)((Query)((Query)((Query)dbHandle.createQuery("select distinct level from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and tenantKey = :tenantKey and deleted = false").bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final int level = rs.getInt("level");
            return QBankUtil.toLevelString(level);
        }).list();
    }
    
    public static List<String> getLevels(final Handle dbHandle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final List<String> docIds, final String tenantKey) {
        return ((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery("select distinct level from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and tenantKey = :tenantKey and deleted = false").bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bindList("docIds", (Iterable)docIds)).bind("tenantKey", tenantKey)).map((rs, ctx) -> {
            final int level = rs.getInt("level");
            return QBankUtil.toLevelString(level);
        }).list();
    }
    
    public static int getNumQuestionsForSubTopicDocs(final Handle dbHandle, final int subjectId, final int topicId, final int subTopicId, final List<String> docIds, final int level) {
        return (int)((Query)((Query)((Query)((Query)((Query)dbHandle.createQuery("select count(*) from questions where topicId in (<topicIds>) and subjectId = :subjectId and subTopicId in (<subTopicIds>) and docid in (<docIds>) and level = :level and tenantKey = :tenantKey and deleted = false").bind("subjectId", subjectId)).bind("topicId", topicId)).bind("subTopicId", subTopicId)).bindList("docIds", (Iterable)docIds)).bind("level", level)).mapTo((Class)Integer.TYPE).one();
    }
    
    public static List<Pair<QuestionConfig, String>> getAnswers(final Handle handle, final List<? extends QuestionConfig> questionConfigs, final String tenantKey) {
        return ((Query)((Query)handle.createQuery("select t.docId, t.qid, t.answer, t.subjectId, t.topicId, t.subTopicId from ( select docId, qid, answer, subjectId, topicId, subTopicId from questions where tenantKey = :tenantKey and deleted = false) as t where (t.docId,t.qid,t.subjectId,t.topicId,t.subTopicId) in (<questionBankIds>)").bind("tenantKey", tenantKey)).bindMethodsList("questionBankIds", (Iterable)questionConfigs, (List)Arrays.asList("getDocId", "getQId", "getSubjectId", "getTopicId", "getSubTopicId"))).map((RowMapper)new RowMapper<Pair<QuestionConfig, String>>() {
            public Pair<QuestionConfig, String> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
                final String docId = rs.getString("docId");
                final int qid = rs.getInt("qid");
                final int subjectId = rs.getInt("subjectId");
                final int topicId = rs.getInt("topicId");
                final int subTopicId = rs.getInt("subTopicId");
                final String answer = rs.getString("answer");
                return (Pair<QuestionConfig, String>)Pair.of((Object)new QuestionConfig(qid, subjectId, topicId, subTopicId, tenantKey, docId), (Object)answer);
            }
        }).list();
    }
    
    public static void softDeleteQuestions(final Handle handle, final String uploadId, final int questionId) {
        ((Update)((Update)handle.createUpdate("update questions set deleted = true where uploadid = :uploadId and qid = :questionId").bind("uploadId", uploadId)).bind("questionId", questionId)).execute();
    }
    
    public static List<DBSubject> getSubjectsForFileUpload(final Handle handle, final String uploadId) {
        return ((Query)handle.createQuery("select distinct s.id, s.name from questions q, subject s where q.subjectid = s.id and q.uploadid = :uploadId and q.deleted = false").bind("uploadId", uploadId)).map((rs, ctx) -> {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            return new DBSubject(name, null, id, -1);
        }).list();
    }
    
    public static List<QBankTopic> getTopicsForFileUploadFromSubjectId(final Handle handle, final String uploadId, final int subjectId) {
        return ((Query)((Query)handle.createQuery("select distinct qbt.id, qbt.name from questions q, subject s, qbanktopics qbt where q.topicid = qbt.id and q.subjectid = :subjectId and q.uploadid = :uploadId and q.deleted = false;").bind("uploadId", uploadId)).bind("subjectId", subjectId)).map((rs, ctx) -> {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            return new QBankTopic(id, name, -1, null);
        }).list();
    }
    
    public static List<QBankSubTopic> getSubTopicsForFileUploadFromTopicId(final Handle handle, final String uploadId, final int topicId) {
        return ((Query)((Query)handle.createQuery("select distinct qbst.id, qbst.name from questions q, qbanktopics qbt, qbanksubtopics qbst where q.topicid = :topicId and q.subtopicId = qbst.id and q.uploadid = :uploadId and q.deleted = false;").bind("uploadId", uploadId)).bind("topicId", topicId)).map((rs, ctx) -> {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            return new QBankSubTopic(id, name, -1, -1, null);
        }).list();
    }
    
    public static List<QBankDBQuestion> getQuestionsForSubTopicFromFileUpload(final Handle handle, final String uploadId, final int subjectId, final int topicId, final int subTopicId) {
        return ((Query)((Query)((Query)((Query)handle.createQuery("select * from questions where uploadid = :uploadId and subjectid = :subjectId and topicid = :topicId and subtopicid = :subTopicId and deleted = false").bind("uploadId", uploadId)).bind("subjectId", subjectId)).bind("topicId", topicId)).bind("subTopicId", subTopicId)).map((rs, ctx) -> new QBankDBQuestion(rs.getInt("qid"), rs.getString("docid"), -1, rs.getString("specifiedqid"), rs.getInt("subjectid"), rs.getInt("topicid"), rs.getInt("subtopicid"), rs.getInt("level"), rs.getString("type"), rs.getString("answer"), rs.getString("qdockey"), rs.getString("uploadid"), rs.getString("tenantkey"))).list();
    }
    
    public static List<QBankDBQuestion> getQuestionsForPart(final Handle handle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final String tenantKey, final List<String> docIds, final int noOfQuestions, final int level) {
        String query;
        if (docIds.isEmpty()) {
            if (level == -1) {
                query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
            }
            else {
                query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
            }
            docIds.add("");
        }
        else if (level == -1) {
            query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
        }
        else {
            query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
        }
        return ((Query)((Query)((Query)((Query)((Query)((Query)((Query)handle.createQuery(query).bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bind("tenantKey", tenantKey)).bind("noOfQuestions", noOfQuestions)).bindList("docIds", (Iterable)docIds)).bind("level", level)).map((rs, ctx) -> new QBankDBQuestion(rs.getInt("qid"), rs.getString("docid"), -1, rs.getString("specifiedqid"), rs.getInt("subjectid"), rs.getInt("topicid"), rs.getInt("subtopicid"), rs.getInt("level"), rs.getString("type"), rs.getString("answer"), rs.getString("qdockey"), rs.getString("uploadid"), rs.getString("tenantkey"))).list();
    }
    
    public static Optional<QBankDBQuestion> changeQuestionForPart(final Handle handle, final int subjectId, final List<Integer> topicIds, final List<Integer> subTopicIds, final String tenantKey, final List<String> docIds, final int noOfQuestions, final int level) {
        String query;
        if (docIds.isEmpty()) {
            if (level == -1) {
                query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
            }
            else {
                query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
            }
            docIds.add("");
        }
        else if (level == -1) {
            query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false ORDER BY RANDOM() LIMIT :noOfQuestions";
        }
        else {
            query = "SELECT * FROM questions WHERE subjectid = :subjectId AND topicid IN (<topicIds>) AND subtopicid IN (<subTopicIds>) AND docid IN (<docIds>) AND tenantkey = :tenantKey AND deleted = false AND level = :level ORDER BY RANDOM() LIMIT :noOfQuestions";
        }
        return ((Query)((Query)((Query)((Query)((Query)((Query)((Query)handle.createQuery(query).bind("subjectId", subjectId)).bindList("topicIds", (Iterable)topicIds)).bindList("subTopicIds", (Iterable)subTopicIds)).bind("tenantKey", tenantKey)).bind("noOfQuestions", noOfQuestions)).bindList("docIds", (Iterable)docIds)).bind("level", level)).map((rs, ctx) -> new QBankDBQuestion(rs.getInt("qid"), rs.getString("docid"), -1, rs.getString("specifiedqid"), rs.getInt("subjectid"), rs.getInt("topicid"), rs.getInt("subtopicid"), rs.getInt("level"), rs.getString("type"), rs.getString("answer"), rs.getString("qdockey"), rs.getString("uploadid"), rs.getString("tenantkey"))).findFirst();
    }
    
    public static void softDeleteQuestionsInFileUpload(final Handle handle, final String uploadId) {
        ((Update)handle.createUpdate("update questions set deleted = true where uploadid = :uploadId").bind("uploadId", uploadId)).execute();
    }
    
    public static Optional<QBankDBQuestion> queryToChangeQuestionForPart(final Handle handle, final String tenantKey, final List<QuestionConfig> removedQuestions, final List<QuestionConfig> selectedQuestions) {
        final List<QuestionConfig> allQIds = new ArrayList<QuestionConfig>(removedQuestions);
        allQIds.addAll(selectedQuestions);
        final StringBuilder rowQuery = new StringBuilder("SELECT * FROM questions WHERE deleted = FALSE AND tenantkey = :tenantKey AND(");
        for (int i = 0; i < allQIds.size(); ++i) {
            rowQuery.append("(subjectid = :subjectId").append(i).append(" AND topicid = :topicId").append(i).append(" AND subtopicid = :subTopicId").append(i).append(" AND qid != :qId").append(i).append(")");
            if (i < allQIds.size() - 1) {
                rowQuery.append(" OR ");
            }
        }
        rowQuery.append(") ORDER BY RANDOM();");
        final Query query = handle.createQuery(rowQuery.toString());
        query.bind("tenantKey", tenantKey);
        int j = 0;
        for (QuestionConfig q : allQIds) {
            ((Query)((Query)((Query)query.bind("subjectId" + j, q.getSubjectId())).bind("topicId" + j, q.getTopicId())).bind("subTopicId" + j, q.getSubTopicId())).bind("qId" + j, q.getQId());
            ++j;
        }
        return query.map((RowMapper)new QuestionMapper()).findFirst();
    }
    
    public static class QuestionMapper implements RowMapper<QBankDBQuestion>
    {
        public QBankDBQuestion map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            final int qId = rs.getInt("qId");
            final String docId = rs.getString("docId");
            final String type = rs.getString("type");
            final int courseId = -1;
            final String specifiedQid = rs.getString("specifiedQid");
            final int level = rs.getInt("level");
            final String answer = rs.getString("answer");
            final String qDocKey = rs.getString("qDocKey");
            final String uploadId = rs.getString("uploadId");
            final String tenantKey = rs.getString("tenantKey");
            final int subjectId = rs.getInt("subjectId");
            final int topicId = rs.getInt("topicId");
            final int subtopicId = rs.getInt("subtopicId");
            return new QBankDBQuestion(qId, docId, courseId, specifiedQid, subjectId, topicId, subtopicId, level, type, answer, qDocKey, uploadId, tenantKey);
        }
    }
}
