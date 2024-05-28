// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.controllers;

import org.jdbi.v3.core.Handle;
import java.util.Optional;
import java.util.Iterator;
import com.upthinkexperts.common.util.QuestionConfig;
import java.util.HashMap;
import com.upthinkexperts.common.responses.ChangeQuestionForPartResponse;
import com.upthinkexperts.common.requests.ChangeQuestionForPartRequest;
import com.upthinkexperts.common.domain.QBankDBQuestion;
import java.util.List;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.QBankDB;
import java.util.ArrayList;
import com.upthinkexperts.common.util.QBankUtil;
import com.upthinkexperts.common.util.CmsCommonConfig;
import com.yojito.minima.api.API;
import com.upthinkexperts.common.responses.GetQuestionsForPartResponse;
import com.upthinkexperts.common.requests.GetQuestionsForPartRequest;
import com.yojito.minima.netty.HttpCall;
import com.yojito.minima.api.Context;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.api.ApiController;

public class QuestionsController extends ApiController
{
    private static final MinimaLogger LOGGER;
    
    public QuestionsController(final Context context) {
        super(context);
    }
    
    @API(path = "/cms/health", corsEnabled = true, authenticated = false)
    public GetQuestionsForPartResponse HealthCheck(final HttpCall call, final GetQuestionsForPartRequest request) {
        QuestionsController.LOGGER.debug("***** Health Check - OK ******", new Object[0]);
        if (request != null) {
            QuestionsController.LOGGER.debug("\n***** request: %s ******\n", new Object[] { request.toStringPretty() });
        }
        return new GetQuestionsForPartResponse();
    }
    
    @API(path = "/cms/part/getQuestions", corsEnabled = true, authenticated = false)
    public GetQuestionsForPartResponse getQuestionsForPartResponse(final HttpCall call, final GetQuestionsForPartRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        return (GetQuestionsForPartResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            int levelValue = -1;
            if (request.getLevel() != null) {
                levelValue = QBankUtil.toLevelInt(request.getLevel());
            }
            List<String> docIds = new ArrayList<String>();
            if (request.getDocIds() != null) {
                docIds = request.getDocIds();
            }
            final List<QBankDBQuestion> questionList = QBankDB.getQuestionsForPart(cmsDbHandle, request.getSubjectId(), request.getTopicIds(), request.getSubTopicIds(), request.getTenantKey(), docIds, request.getNoOfQuestions(), levelValue);
            return new GetQuestionsForPartResponse(questionList);
        });
    }
    
    @API(path = "/cms/part/changeQuestion", corsEnabled = true, authenticated = false)
    public ChangeQuestionForPartResponse changeQuestionForPartResponse(final HttpCall call, final ChangeQuestionForPartRequest request) {
        final CmsCommonConfig config = (CmsCommonConfig)this.context.get("cmsCommonConfig");
        final HashMap<Integer, Integer> topicIds = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> subTopicIds = new HashMap<Integer, Integer>();
        final HashMap<String, Integer> docIds = new HashMap<String, Integer>();
        for (final QuestionConfig qc : request.getFixedQuestions()) {
            topicIds.put(qc.getTopicId(), qc.getQId());
            subTopicIds.put(qc.getSubTopicId(), qc.getQId());
            docIds.put(qc.getDocId(), qc.getQId());
        }
        return (ChangeQuestionForPartResponse)Database.withinTx(config.getDb(), cmsDbHandle -> {
            int levelValue = -1;
            if (request.getLevel() != null) {
                levelValue = QBankUtil.toLevelInt(request.getLevel());
            }
            while (true) {
                final Optional<QBankDBQuestion> question = QBankDB.changeQuestionForPart(cmsDbHandle, request.getSubjectId(), request.getTopicIds(), request.getSubTopicIds(), request.getTenantKey(), request.getDocIds(), 1, levelValue);
                if (question.isPresent()) {
                    if (QBankUtil.isIdSame(request.getSubjectId(), question.get().getSubjectId()) && QBankUtil.isIdSameIfContains(topicIds, question.get().getTopicId(), question.get().getId()) && QBankUtil.isIdSameIfContains(subTopicIds, question.get().getSubTopicId(), question.get().getId()) && QBankUtil.isIdSameIfContainsString(docIds, question.get().getDocId(), question.get().getId())) {
                        continue;
                    }
                    else {
                        return new ChangeQuestionForPartResponse(question.get());
                    }
                }
                else {
                    new ChangeQuestionForPartResponse(new QBankDBQuestion());
                    return;
                }
            }
        });
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QuestionsController.class);
    }
}
