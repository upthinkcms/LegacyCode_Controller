// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import com.upthinkexperts.common.domain.QBankOption;
import com.amazonaws.services.s3.AmazonS3;
import com.upthinkexperts.common.domain.KnowledgeDoc;
import com.upthinkexperts.common.domain.DocResources;
import com.upthinkexperts.common.domain.DocSectionType;
import com.upthinkexperts.common.db.DocDB;
import com.upthinkexperts.common.domain.DocSection;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.upthinkexperts.common.parsing.DocPart;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;
import com.upthinkexperts.common.domain.QBankDoc;
import java.util.Optional;
import com.upthinkexperts.common.domain.QBankSubTopic;
import com.upthinkexperts.common.domain.QBankTopic;
import com.upthinkexperts.common.db.QBankDB;
import com.upthinkexperts.common.db.SubjectDB;
import com.upthinkexperts.common.domain.QBankDBQuestion;
import com.upthinkexperts.common.domain.QBankQuestion;
import org.jdbi.v3.core.Handle;
import com.yojito.minima.logging.MinimaLogger;

public class QBankUtil
{
    private static final MinimaLogger LOGGER;
    
    public static QBankDBQuestion toDBQuestion(final Handle dbHandle, final QBankQuestion question, final String qDocKey, final String uploadId) {
        final int subjectId = SubjectDB.getSubjectId(dbHandle, question.getSubject());
        final int courseId = -1;
        int topicId = -1;
        int subTopicId = -1;
        final Optional<QBankTopic> qBankTopicOptional = QBankDB.getTopicByName(dbHandle, subjectId, question.getTopic(), question.getTenantKey());
        if (qBankTopicOptional.isEmpty()) {
            QBankUtil.LOGGER.debug("Inserting topic named %s for subjectId %d", new Object[] { question.getTopic(), subjectId });
            topicId = QBankDB.insertTopic(dbHandle, question.getTopic(), subjectId, question.getTenantKey());
        }
        else {
            topicId = qBankTopicOptional.get().getId();
        }
        final Optional<QBankSubTopic> qBankSubTopicOptional = QBankDB.getSubTopicForSubjectAndTopic(dbHandle, subjectId, topicId, question.getSubTopic(), question.getTenantKey());
        if (qBankSubTopicOptional.isEmpty()) {
            if (question.getSubTopic() != null && !question.getSubTopic().isEmpty()) {
                QBankUtil.LOGGER.debug("Inserting sub-topic named %s for subjectId %d and topic %s", new Object[] { question.getSubTopic(), subjectId, question.getTopic() });
                subTopicId = QBankDB.insertSubTopic(dbHandle, question.getSubTopic(), subjectId, topicId, question.getTenantKey());
            }
        }
        else {
            subTopicId = qBankSubTopicOptional.get().getId();
        }
        int level = -1;
        final String lowerCase = question.getLevel().toLowerCase();
        switch (lowerCase) {
            case "na": {
                level = -1;
                break;
            }
            case "easy": {
                level = 1;
                break;
            }
            case "medium": {
                level = 5;
                break;
            }
            case "difficult": {
                level = 10;
                break;
            }
            default: {
                throw new RuntimeException("Unsupported question level - <" + question.getLevel());
            }
        }
        return new QBankDBQuestion(question.getId(), question.getDocId(), courseId, question.getSpecifiedQid(), subjectId, topicId, subTopicId, level, question.getType(), question.getAnswer(), qDocKey, uploadId, question.getTenantKey());
    }
    
    public static Map<Pair<String, Integer>, List<DocPart.DocRun>> retrieveImages(final QBankDoc qBankDoc, final QBankDBQuestion q) {
        final Map<Pair<String, Integer>, List<DocPart.DocRun>> imageMap = new HashMap<Pair<String, Integer>, List<DocPart.DocRun>>();
        qBankDoc.getQuestions().stream().filter(question -> q.getDocId().equals(question.getDocId()) && q.getId() == question.getId()).forEach(question -> {
            final List<DocPart.DocRun> imageRuns = new ArrayList<DocPart.DocRun>();
            final DocSection questionDoc = question.getQuestion();
            imageRuns.addAll(retrieveImages(questionDoc));
            question.getOptionList().forEach(qBankOption -> imageRuns.addAll(retrieveImages(qBankOption.getContent())));
            imageMap.put(Pair.of((Object)question.getDocId(), (Object)q.getId()), imageRuns);
            return;
        });
        return imageMap;
    }
    
    private static List<DocPart.DocRun> retrieveImages(final DocSection questionDoc) {
        final List<DocPart.DocRun> imageRuns = new ArrayList<DocPart.DocRun>();
        questionDoc.getParagraphs().forEach(p -> {
            switch (p.getType()) {
                case IMAGE: {
                    imageRuns.add(p);
                    break;
                }
                case P: {
                    p.getParagraphs().forEach(docRun -> {
                        if (docRun.getType().equals(DocPart.DocRunType.IMAGE)) {
                            imageRuns.add(docRun);
                        }
                        return;
                    });
                    break;
                }
            }
            return;
        });
        if (questionDoc.getSections() != null) {
            questionDoc.getSections().forEach(docSection -> imageRuns.addAll(retrieveImages(docSection)));
        }
        return imageRuns;
    }
    
    public static void upsertRootDoc(final Handle handle, final QBankDoc qBankDoc) {
        final Optional<KnowledgeDoc> docOptional = DocDB.loadDoc(handle, qBankDoc.getDocId());
        if (docOptional.isEmpty()) {
            final DocSection empty = new DocSection(DocSectionType.Doc, null, null);
            final DocResources emptyResources = new DocResources(null, null);
            final KnowledgeDoc doc = new KnowledgeDoc(qBankDoc.getDocId(), empty, true, "QBANK", "QBANK", "QBANK", "EASY", null, emptyResources);
            DocDB.upsertDoc(handle, doc);
        }
    }
    
    public Pair<List<Pair<String, Integer>>, List<Pair<String, Integer>>> buildUpdateList(final Handle dbHandle, final List<QBankDBQuestion> questions) {
        final List<Pair<String, Integer>> insertList = new ArrayList<Pair<String, Integer>>();
        final List<Pair<String, Integer>> updateList = new ArrayList<Pair<String, Integer>>();
        questions.forEach(q -> {
            final Optional<QBankDBQuestion> dbQuestion = QBankDB.loadQuestion(dbHandle, q.getId(), q.getDocId(), q.getTenantKey(), q.getSubjectId(), q.getTopicId(), q.getSubTopicId());
            if (dbQuestion.isEmpty()) {
                insertList.add(Pair.of((Object)q.getDocId(), (Object)q.getId()));
            }
            else {
                updateList.add(Pair.of((Object)q.getDocId(), (Object)q.getId()));
            }
            return;
        });
        return (Pair<List<Pair<String, Integer>>, List<Pair<String, Integer>>>)Pair.of((Object)insertList, (Object)updateList);
    }
    
    public static void uploadQuestionContent(final AmazonS3 s3Client, final String bucket, final QBankQuestion questionDoc, final String jobId, final String qDocKey) {
        final String key = String.format("%s/%s/%s-content.json", jobId, questionDoc.getDocId(), qDocKey);
        final String content = questionDoc.toStringPretty();
        s3Client.putObject(bucket, key, content);
        QBankUtil.LOGGER.info("Uploaded Question Content %s.%s", new Object[] { questionDoc.getDocId(), questionDoc.getId() });
    }
    
    public static String toLevelString(final int level) {
        switch (level) {
            case -1: {
                return "na";
            }
            case 1: {
                return "easy";
            }
            case 5: {
                return "medium";
            }
            case 10: {
                return "difficult";
            }
            default: {
                throw new RuntimeException("Unknown level - " + level);
            }
        }
    }
    
    public static int toLevelInt(final String levelStr) {
        int level = -1;
        final String lowerCase = levelStr.toLowerCase();
        switch (lowerCase) {
            case "na": {
                level = -1;
                break;
            }
            case "easy": {
                level = 1;
                break;
            }
            case "medium": {
                level = 5;
                break;
            }
            case "difficult": {
                level = 10;
                break;
            }
            default: {
                throw new RuntimeException("Unsupported question level - <" + levelStr);
            }
        }
        return level;
    }
    
    public static boolean isIdSame(final int firstId, final int secondId) {
        return firstId == secondId;
    }
    
    public static boolean isIdSameIfContains(final HashMap<Integer, Integer> ids, final int firstId, final int secondId) {
        return ids.containsKey(firstId) && ids.get(firstId) == secondId;
    }
    
    public static boolean isIdSameIfContainsString(final HashMap<String, Integer> ids, final String docId, final int secondId) {
        return ids.containsKey(docId) && ids.get(docId) == secondId;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankUtil.class);
    }
}
