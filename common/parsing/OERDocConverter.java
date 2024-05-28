// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import com.upthinkexperts.common.domain.oer.OERReferences;
import com.upthinkexperts.common.domain.oer.OERPractiseExample;
import com.upthinkexperts.common.domain.oer.OERExample;
import com.upthinkexperts.common.domain.oer.OERDefinition;
import com.upthinkexperts.common.domain.DocSection;
import com.upthinkexperts.common.domain.oer.OERSectionExercise;
import java.util.HashMap;
import com.upthinkexperts.common.domain.DocSectionType;
import com.upthinkexperts.common.domain.oer.OERNode;
import com.upthinkexperts.common.domain.oer.OERReview;
import com.upthinkexperts.common.domain.oer.OERLearningObjective;
import com.upthinkexperts.common.domain.DocResources;
import com.upthinkexperts.common.domain.oer.OERSectionExerciseSet;
import com.upthinkexperts.common.domain.oer.OERContent;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Map;
import com.upthinkexperts.common.domain.oer.OERPrerequisite;
import java.util.List;
import com.upthinkexperts.common.domain.oer.OERDocument;

public class OERDocConverter
{
    public OERDocument convert(final String docName, final ParsedDoc parsedDoc, final String tenantKey) {
        final Map<String, String> documentAttributes = this.findDocAttributes(parsedDoc);
        final DocPart start = parsedDoc.getParts().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.START)).findFirst().get();
        final DocPart document = ConverterUtil.getChildrenOfType(start, DocPartTypes.openstax_document, true);
        final DocPart leadingObjectiveDP = ConverterUtil.getChildrenOfType(document, DocPartTypes.openstax_learningobjective);
        final List<DocPart> prerequisiteDPList = ConverterUtil.getAllChildrenOfType(document, DocPartTypes.openstax_prerequisite);
        final List<DocPart> contentDPList = ConverterUtil.getAllChildrenOfType(document, DocPartTypes.openstax_content);
        final List<DocPart> exerciseSetDPList = ConverterUtil.getAllChildrenOfType(document, DocPartTypes.openstax_exerciseset);
        final DocPart reviewDP = ConverterUtil.getChildrenOfType(document, DocPartTypes.openstax_review, false);
        final String docId = documentAttributes.get("docId");
        final String title = documentAttributes.get("title");
        final String book = documentAttributes.get("book");
        final String subject = documentAttributes.get("subject");
        final String subTopic = documentAttributes.get("subTopic");
        final String topic = documentAttributes.get("topic");
        final String indexingId = documentAttributes.get("indexingId");
        final OERLearningObjective learningObjective = this.mapLearningobjective(leadingObjectiveDP, documentAttributes, tenantKey);
        List<OERPrerequisite> prerequisiteList = null;
        List<OERContent> contentList = null;
        List<OERSectionExerciseSet> exerciseSetList = null;
        if (prerequisiteDPList != null) {
            prerequisiteList = prerequisiteDPList.stream().map(p -> this.mapPrerequisite(p, documentAttributes, tenantKey)).collect((Collector<? super Object, ?, List<OERPrerequisite>>)Collectors.toList());
        }
        if (contentDPList != null) {
            contentList = contentDPList.stream().map(p -> this.mapContent(p, documentAttributes, tenantKey)).collect((Collector<? super Object, ?, List<OERContent>>)Collectors.toList());
        }
        if (exerciseSetDPList != null) {
            exerciseSetList = exerciseSetDPList.stream().map(p -> this.mapExerciseSet(p, documentAttributes, tenantKey)).collect((Collector<? super Object, ?, List<OERSectionExerciseSet>>)Collectors.toList());
        }
        final OERReview review = (reviewDP != null) ? this.mapReview(reviewDP, documentAttributes, tenantKey) : null;
        return new OERDocument(docId, title, subject, book, topic, subTopic, indexingId, learningObjective, prerequisiteList, contentList, new DocResources(""), exerciseSetList, review);
    }
    
    private Map<String, String> findDocAttributes(final ParsedDoc qbank) {
        final DocPart start = qbank.getParts().stream().filter(docPart -> docPart.getType().equals(DocPartTypes.START)).findFirst().get();
        final DocPart document = ConverterUtil.getChildrenOfType(start, DocPartTypes.openstax_document);
        return document.getAttributes();
    }
    
    private OERContent mapContent(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        final List<OERNode> nodes = qp.getChildren().stream().map(dp -> {
            switch (dp.getType()) {
                case openstax_definition: {
                    return this.mapDefinition(dp, rootAttributes, tenantKey, dp.getAttributes());
                }
                case openstax_example: {
                    return this.mapExample(dp, rootAttributes, tenantKey, dp.getAttributes());
                }
                case openstax_practiseexample: {
                    return this.mapPractiseExample(dp, rootAttributes, tenantKey, dp.getAttributes());
                }
                case openstax_bookmarks: {
                    return this.mapBookmarks(dp, rootAttributes, tenantKey, dp.getAttributes());
                }
                default: {
                    throw new RuntimeException("Unknown node - " + dp.getType());
                }
            }
        }).collect((Collector<? super Object, ?, List<OERNode>>)Collectors.toList());
        return new OERContent(nodes, qp.paragraphs, this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERLearningObjective mapLearningobjective(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        return new OERLearningObjective(ConverterUtil.toDocSection(DocSectionType.LearningObjective, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private Map<String, String> mergeAttributes(final Map<String, String> rootAttributes, final DocPart qp) {
        final Map<String, String> attributes = new HashMap<String, String>();
        attributes.putAll(rootAttributes);
        if (qp.getAttributes() != null) {
            attributes.putAll(qp.getAttributes());
        }
        return attributes;
    }
    
    private OERPrerequisite mapPrerequisite(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        return new OERPrerequisite(ConverterUtil.toDocSection(DocSectionType.Prerequisite, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERSectionExerciseSet mapExerciseSet(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        final List<OERSectionExercise> exerciseList = ConverterUtil.getAllChildrenOfType(qp, DocPartTypes.openstax_exercise).stream().map(p -> this.mapExercise(p, rootAttributes, tenantKey)).collect((Collector<? super Object, ?, List<OERSectionExercise>>)Collectors.toList());
        final DocSection section = ConverterUtil.toDocSection(DocSectionType.SectionExerciseSet, qp);
        return new OERSectionExerciseSet(section, this.mergeAttributes(rootAttributes, qp), exerciseList);
    }
    
    private OERSectionExercise mapExercise(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        return new OERSectionExercise(ConverterUtil.toDocSection(DocSectionType.SectionExercise, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERReview mapReview(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey) {
        return new OERReview(ConverterUtil.toDocSection(DocSectionType.Review, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERDefinition mapDefinition(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey, final Map<String, String> dpAttributes) {
        return new OERDefinition(ConverterUtil.toDocSection(DocSectionType.Definition, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERExample mapExample(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey, final Map<String, String> dpAttributes) {
        return new OERExample(ConverterUtil.toDocSection(DocSectionType.Example, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERPractiseExample mapPractiseExample(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey, final Map<String, String> dpAttributes) {
        return new OERPractiseExample(ConverterUtil.toDocSection(DocSectionType.PractiseExample, qp), this.mergeAttributes(rootAttributes, qp));
    }
    
    private OERReferences mapBookmarks(final DocPart qp, final Map<String, String> rootAttributes, final String tenantKey, final Map<String, String> dpAttributes) {
        return new OERReferences(ConverterUtil.toDocSection(DocSectionType.References, qp), this.mergeAttributes(rootAttributes, qp));
    }
}
