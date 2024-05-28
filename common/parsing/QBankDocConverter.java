// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import com.upthinkexperts.common.domain.DocSection;
import com.upthinkexperts.common.domain.QBankOption;
import com.upthinkexperts.common.domain.DocSectionType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Optional;
import com.upthinkexperts.common.domain.QBankQuestion;
import java.util.List;
import com.upthinkexperts.common.domain.DocResources;
import com.upthinkexperts.common.domain.QBankDoc;
import com.yojito.minima.logging.MinimaLogger;

public class QBankDocConverter
{
    private static final MinimaLogger LOGGER;
    
    public QBankDoc convert(final String docName, final ParsedDoc qbank, final String tenantKey) {
        final String docId = this.findDocId(qbank);
        final List<QBankQuestion> questions = this.mapQuestions(docId, qbank, tenantKey);
        final QBankDoc knowledgeDoc = new QBankDoc(docId, new DocResources(""), questions);
        return knowledgeDoc;
    }
    
    private String findDocId(final ParsedDoc qbank) {
        final DocPart start = qbank.getParts().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.START)).findFirst().get();
        final Optional<DocPart> docIdPart = start.getChildren().stream().filter(dp -> dp.getType().equals(DocPartTypes.docid)).findFirst();
        if (docIdPart.isEmpty()) {
            throw new RuntimeException("DocIds is missing in the doc File");
        }
        final String realDocId = ConverterUtil.retriveText(docIdPart.get());
        QBankDocConverter.LOGGER.debug("Retrieved DocId = %s", new Object[] { realDocId });
        return realDocId;
    }
    
    private List<QBankQuestion> mapQuestions(final String docId, final ParsedDoc qbank, final String tenantKey) {
        final List<QBankQuestion> questions = new ArrayList<QBankQuestion>();
        final DocPart start = qbank.getParts().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.START)).findFirst().get();
        final List<DocPart> questionParts = start.getChildren().stream().filter(dp -> dp.getType().equals(DocPartTypes.questionanswer)).collect((Collector<? super Object, ?, List<DocPart>>)Collectors.toList());
        System.out.println("questionParts DocPart " + questionParts.size());
        questionParts.stream().forEach(qp -> questions.add(this.mapQuestionPart(docId, qp, tenantKey)));
        return questions;
    }
    
    private QBankQuestion mapQuestionPart(final String docId, final DocPart qp, final String tenantKey) {
        String id = null;
        try {
            id = ConverterUtil.getTextOfChildrenOfType(qp, DocPartTypes.id);
            System.out.println("id " + Integer.parseInt(id));
            String course = ConverterUtil.getTextOfChildrenOfType(qp, DocPartTypes.course, false);
            if (course == null) {
                course = "Global";
            }
            System.out.println("course " + course);
            final DocPart specifiedqidPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.specifiedqid)).findFirst().orElse(null);
            final String specifiedqid = (specifiedqidPart != null) ? ConverterUtil.retriveText(specifiedqidPart) : null;
            System.out.println("specifiedqid " + specifiedqid);
            final String subject = ConverterUtil.getTextOfChildrenOfType(qp, DocPartTypes.subject);
            System.out.println("subject " + subject);
            final DocPart topicPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.topic)).findFirst().get();
            final Optional<DocPart> subTopicPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.subtopic)).findFirst();
            final DocPart typePart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.type)).findFirst().get();
            final DocPart levelPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.level)).findFirst().get();
            final DocPart answerPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.answer)).findFirst().get();
            final DocPart questionPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.question)).findFirst().get();
            final DocPart optionsPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.options)).findFirst().get();
            final List<DocPart> optionsPartList = optionsPart.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.option)).collect((Collector<? super Object, ?, List<DocPart>>)Collectors.toList());
            final Optional<DocPart> generalFeedbackPart = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.generalfeedback)).findFirst();
            DocSection generalFeedback = null;
            if (generalFeedbackPart.isPresent()) {
                generalFeedback = ConverterUtil.toDocSection(null, generalFeedbackPart.get());
            }
            List<QBankOption> specificFeebackList = null;
            final Optional<DocPart> specificfeedbacks = qp.getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.specificfeedbacks)).findFirst();
            if (specificfeedbacks.isPresent()) {
                final List<DocPart> specificFeebackPartList = specificfeedbacks.get().getChildren().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.specificfeedback)).collect((Collector<? super Object, ?, List<DocPart>>)Collectors.toList());
                specificFeebackList = specificFeebackPartList.stream().map(q -> this.toQBankOption(q)).collect((Collector<? super Object, ?, List<QBankOption>>)Collectors.toList());
            }
            final DocSection question = ConverterUtil.toDocSection(null, questionPart);
            final String topic = ConverterUtil.retriveText(topicPart);
            final String subTopic = subTopicPart.isPresent() ? ConverterUtil.retriveText(subTopicPart.get()) : null;
            final String type = ConverterUtil.retriveText(typePart);
            final String level = ConverterUtil.retriveText(levelPart);
            final String answer = ConverterUtil.retriveText(answerPart);
            System.out.println("topic " + topic);
            System.out.println("subTopic " + subTopic);
            System.out.println("type " + type);
            System.out.println("level " + level);
            System.out.println("answer " + answer);
            System.out.println("question " + question.toStringPretty());
            final List<QBankOption> optionList = optionsPartList.stream().map(q -> this.toQBankOption(q)).collect((Collector<? super Object, ?, List<QBankOption>>)Collectors.toList());
            return new QBankQuestion(Integer.parseInt(id), docId, course, specifiedqid, subject, topic, subTopic, level, type, question, optionList, answer, generalFeedback, specificFeebackList, tenantKey);
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (id != null) {
                QBankDocConverter.LOGGER.error((Throwable)e, "Error for while mapping question id - %s - qp - %s", new Object[] { id, qp });
            }
            else {
                QBankDocConverter.LOGGER.error((Throwable)e, "error for " + qp, new Object[0]);
            }
            throw e;
        }
    }
    
    private QBankOption toQBankOption(final DocPart option) {
        final DocPart contentPart = ConverterUtil.getChildrenOfType(option, DocPartTypes.content);
        final String label = ConverterUtil.getTextOfChildrenOfType(option, DocPartTypes.label);
        System.out.println("label " + label);
        return new QBankOption(label, ConverterUtil.toDocSection(null, contentPart));
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankDocConverter.class);
    }
}
