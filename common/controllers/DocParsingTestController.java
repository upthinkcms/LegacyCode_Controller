// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.controllers;

import com.yojito.minima.gson.GsonDto;
import java.io.IOException;
import com.yojito.minima.gson.GsonArray;
import com.yojito.minima.gson.GsonObject;
import java.util.Objects;
import com.upthinkexperts.common.util.MathTypeEquationProcessor;
import com.upthinkexperts.common.responses.GetEquationsResponse;
import com.upthinkexperts.common.requests.GetEquationsRequest;
import com.upthinkexperts.common.domain.DocSectionType;
import com.upthinkexperts.common.parsing.DocPartTypes;
import java.util.Collection;
import java.util.ArrayList;
import com.upthinkexperts.common.parsing.DocPart;
import com.yojito.minima.api.API;
import com.upthinkexperts.common.parsing.ParsedDoc;
import com.upthinkexperts.common.domain.DocSection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import com.upthinkexperts.common.parsing.PoiDocParser;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import java.io.File;
import com.upthinkexperts.common.requests.DocParsingTestRequest;
import com.yojito.minima.netty.HttpCall;
import com.yojito.minima.api.Context;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.api.ApiController;

public class DocParsingTestController extends ApiController
{
    private static final MinimaLogger LOGGER;
    String TEMP_DIR;
    
    public DocParsingTestController(final Context context) {
        super(context);
        this.TEMP_DIR = "/work/myworkspace/druta/projects/upthinkexperts/dump/doc-viewer/public/files/";
    }
    
    @API(path = "/docParsingTest/getDoc", corsEnabled = true, authenticated = false)
    public DocViewerResponse getDoc(final HttpCall call, final DocParsingTestRequest request) {
        try {
            FileUtils.deleteDirectory(new File(this.TEMP_DIR));
            final String docId = request.getDocId();
            final String tempPathPrefix = this.TEMP_DIR + "olebin/" + docId;
            Files.createDirectories(Paths.get(this.TEMP_DIR + "olebin/" + docId, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            final String imagesPathPrefix = this.TEMP_DIR + "images/" + docId;
            Files.createDirectories(Paths.get(this.TEMP_DIR + "images/" + docId, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            final PoiDocParser docParser = new PoiDocParser();
            final ParsedDoc parsedDoc = docParser.parse(docId, false, ParsingSpecs.OPENSTAX, new File(request.getDirectory(), request.getFileName()), tempPathPrefix, imagesPathPrefix);
            return new DocViewerResponse((List<DocSection>)parsedDoc.getParts().stream().map(p -> this.toSection(p)).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private DocSection toSection(final DocPart docPart) {
        final List<DocPart.DocRun> paragraphs = new ArrayList<DocPart.DocRun>();
        List<DocSection> sections = new ArrayList<DocSection>();
        paragraphs.addAll(docPart.getParagraphs());
        if (docPart.getChildren() != null) {
            sections = docPart.getChildren().stream().map(p -> this.toSection(p)).collect((Collector<? super Object, ?, List<DocSection>>)Collectors.toList());
        }
        return new DocSection(this.toOERType(docPart.getType()), sections, paragraphs);
    }
    
    private DocSectionType toOERType(final DocPartTypes partType) {
        switch (partType) {
            case openstax_document: {
                return DocSectionType.Doc;
            }
            case openstax_learningobjective: {
                return DocSectionType.LearningObjective;
            }
            case openstax_prerequisite: {
                return DocSectionType.Prerequisite;
            }
            case openstax_content: {
                return DocSectionType.Explanation;
            }
            case openstax_definition: {
                return DocSectionType.Definition;
            }
            case openstax_example: {
                return DocSectionType.Example;
            }
            case openstax_practiseexample: {
                return DocSectionType.PractiseExample;
            }
            case openstax_bookmarks: {
                return DocSectionType.References;
            }
            case openstax_exerciseset: {
                return DocSectionType.SectionExerciseSet;
            }
            case openstax_exercise: {
                return DocSectionType.SectionExercise;
            }
            case openstax_review: {
                return DocSectionType.Review;
            }
            default: {
                return DocSectionType.Doc;
            }
        }
    }
    
    @API(path = "/docParsingTest/getEquations", corsEnabled = true, authenticated = false)
    public GetEquationsResponse getEquations(final HttpCall call, final GetEquationsRequest request) throws IOException, InterruptedException {
        final MathTypeEquationProcessor equationProcessor = new MathTypeEquationProcessor((String)this.context.get("calabashPath"), (String)this.context.get("jdkPath"));
        final String OLE_BIN_PREFIX = String.format("%s%s/%s", this.TEMP_DIR, "olebin", request.getDocId());
        if (Objects.requireNonNull(new File(OLE_BIN_PREFIX).list()).length > 0) {
            final String MML_PREFIX = String.format("%s%s/%s", this.TEMP_DIR, "mml", request.getDocId());
            equationProcessor.transformEquations(OLE_BIN_PREFIX, MML_PREFIX);
            final GsonObject equationJson = equationProcessor.createEquationJson(MML_PREFIX, request.getDocId());
            return new GetEquationsResponse(equationJson);
        }
        final GsonObject noEquations = new GsonObject();
        final GsonArray as = new GsonArray();
        final GsonObject message = new GsonObject();
        message.put("message", "No Failed Equations");
        as.add(message);
        noEquations.put("as", as);
        return new GetEquationsResponse(noEquations);
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)DocParsingTestController.class);
    }
    
    public static class DocViewerResponse extends GsonDto
    {
        private final List<DocSection> sections;
        
        public DocViewerResponse(final List<DocSection> sections) {
            this.sections = sections;
        }
    }
}
