// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import com.yojito.minima.gson.GsonObject;
import java.nio.file.Paths;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import java.io.IOException;
import com.upthinkexperts.common.domain.QBankDoc;
import com.yojito.minima.api.Context;
import java.util.Properties;
import com.upthinkexperts.common.util.ParsedDocUtil;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import java.io.FileInputStream;
import com.upthinkexperts.Bootstrap;
import java.util.UUID;
import java.io.File;
import com.yojito.minima.logging.MinimaLogger;

public class QBankParser
{
    private static final MinimaLogger LOGGER;
    
    public static void main(final String[] args) throws IOException {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/docs/qbank/byCourseName";
        final String fileName = "test1.docx";
        final String docId = "TEST_DOC_ID_1";
        final String tempPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/olebin/" + docId;
        final String imagesPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/images/" + docId;
        final File tempPathDir = new File(tempPathPrefix);
        final File imagePathDir = new File(imagesPathPrefix);
        if (!tempPathDir.exists()) {
            tempPathDir.mkdirs();
        }
        if (!imagePathDir.exists()) {
            imagePathDir.mkdirs();
        }
        final String jobId = UUID.randomUUID().toString();
        try {
            final Properties properties = Bootstrap.readAndPrintProperties("Starting CMS Server for [ %S ] ENVIRONMENT", args);
            final Context context = Bootstrap.buildContext(properties);
            final File file = new File(directory, fileName);
            final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            final ParsedDoc doc = parse(docId, true, ParsingSpecs.QBANK, fis, tempPathPrefix, imagesPathPrefix);
            final PrintStream writetoEngineer = new PrintStream(new FileOutputStream("QBank.json", false));
            ParsedDocUtil.printParsedDoc(writetoEngineer, doc);
            final QBankDocConverter converter = new QBankDocConverter();
            final QBankDoc qBankDoc = converter.convert(docId, doc, "_CMS_");
            System.out.println(qBankDoc.toStringPretty());
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Q Error for [" + fileName + "] -" + e.getMessage());
        }
    }
    
    public static ParsedDoc parse(final String docName, final boolean isQuestion, final ParsingSpec parsingSpec, final InputStream fis, final String equationTempPath, final String tempDirpath) throws IOException {
        final XWPFDocument document = new XWPFDocument(fis);
        final DocxRelationshipProcessor relationshipProcessor = new DocxRelationshipProcessor(ParsingSpec.DocFormat.QBANK, document, equationTempPath, tempDirpath);
        relationshipProcessor.process(docName, isQuestion);
        final ParsedDoc parsedDoc = new ParsedDoc(docName, new ArrayList<DocPart>(), relationshipProcessor.getDocImages());
        final DocPart root = new DocPart(null, DocPartTypes.START, new ArrayList<DocPart.DocRun>());
        parsedDoc.add(root);
        DocPart current = root;
        int tabs = 0;
        final List<IBodyElement> body = document.getBodyElements();
        final int defaultFontSize = -1;
        final AtomicInteger atomicInteger = new AtomicInteger();
        String qId = null;
        try {
            for (final IBodyElement element : body) {
                atomicInteger.incrementAndGet();
                if (element instanceof XWPFParagraph) {
                    QBankParser.LOGGER.debug("Paragraph Text = [%s]", new Object[] { ((XWPFParagraph)element).getText() });
                }
                else if (element instanceof XWPFTable) {
                    QBankParser.LOGGER.debug("Table detected", new Object[0]);
                }
                else {
                    QBankParser.LOGGER.debug("Different type = [%s]", new Object[] { element.getElementType() });
                }
                current.startParagraph();
                final List<DocPart.DocRun> list = PoiDocParser.paragraphToDocRuns(element, relationshipProcessor, defaultFontSize);
                for (final DocPart.DocRun docRun : list) {
                    switch (docRun.getType()) {
                        case EQUATION: {
                            current.addEquationRun(docRun);
                            continue;
                        }
                        case IMAGE: {
                            current.addImageRun(docRun);
                            continue;
                        }
                        case TABLE: {
                            current.addTableRun(docRun);
                            continue;
                        }
                        case TEXT: {
                            final String paragraphText = docRun.getText();
                            final CompositeDetails composition = parsingSpec.checkComposite(paragraphText, qId);
                            QBankParser.LOGGER.trace("isComposite : [%s] : [%b] -> DocPartType : [%s] -> TagText : [%s] -> QID : [%s] ", new Object[] { paragraphText, composition.isComposite(), composition.getDocPartTypes(), composition.getText(), qId });
                            if (!composition.isComposite()) {
                                final boolean isMarker = parsingSpec.isMarker(paragraphText);
                                if (isMarker) {
                                    final boolean isStart = parsingSpec.isMarkerStart(paragraphText);
                                    final boolean isEnd = parsingSpec.isMarkerEnd(paragraphText);
                                    if (isStart) {
                                        final DocPartTypes type = DocPartTypes.getType(paragraphText);
                                        QBankParser.LOGGER.trace("%sSTART %s---------------------------------------------------------------------\n", new Object[] { TABS(tabs), type.types });
                                        QBankParser.LOGGER.trace("CURRENT.TYPE = %s, TYPE = %s", new Object[] { current.getType(), type });
                                        if (!parsingSpec.checkHierarchy(current.getType(), type)) {
                                            if (qId == null) {
                                                qId = "0";
                                            }
                                            throw new RuntimeException(String.format("Wrong hierarchy (Question ID: %s) -> Either tags are not closed (probably due to spelling mistake or you forgot to add closing tag) : Child Type %s, Parent Type %s", qId, type, current.getType()));
                                        }
                                        current = current.child(type);
                                        ++tabs;
                                    }
                                    else if (isEnd) {
                                        final DocPartTypes type = DocPartTypes.getType(paragraphText);
                                        --tabs;
                                        QBankParser.LOGGER.trace("%sEND %s---------------------------------------------------------------------\n", new Object[] { TABS(tabs), type.types });
                                        QBankParser.LOGGER.trace("END TYPE %s", new Object[] { current.getType() });
                                        current.end();
                                        current = current.getParent();
                                    }
                                }
                                if (paragraphText == null || isMarker) {
                                    continue;
                                }
                                current.addTextRun(paragraphText, docRun.isBold(), docRun.isItalic(), docRun.getUnderline(), docRun.getFontName(), docRun.getFontColor(), docRun.getFontSize());
                                QBankParser.LOGGER.trace("ParagraphText => [%s] [%d] -> [%s]", new Object[] { TABS(tabs), current.paragraphs.size(), paragraphText });
                                continue;
                            }
                            final DocPartTypes type2 = composition.getDocPartTypes();
                            final String text = composition.getText();
                            if (composition.getQId() != null) {
                                qId = composition.getQId();
                            }
                            if (parsingSpec.checkHierarchy(current.getType(), type2)) {
                                final DocPart newChild = current.child(type2);
                                newChild.addTextRun(text, docRun.isBold(), docRun.isItalic(), docRun.getUnderline(), docRun.getFontName(), docRun.getFontColor(), docRun.getFontSize());
                                newChild.end();
                                continue;
                            }
                            if (qId == null) {
                                qId = "0";
                            }
                            throw new RuntimeException(String.format("Wrong hierarchy (Question ID: %s) -> Either tags are not closed (probably due to spelling mistake or you forgot to add closing tag) : Child Type %s, Parent Type %s, Node: %s", qId, type2, current.getType(), current));
                        }
                        default: {
                            QBankParser.LOGGER.info("docRun Type = %s", new Object[] { docRun.getType() });
                            continue;
                        }
                    }
                }
                current.endParagraph();
                System.out.println();
            }
            final File rootDirectory = Paths.get(tempDirpath, new String[0]).getParent().toFile();
            final GsonObject mapping = new GsonObject();
            relationshipProcessor.getOleToImageEqMap().forEach((o, i) -> mapping.put(o, i));
            Files.writeString(Paths.get(rootDirectory.getAbsolutePath(), String.format("%s-oleImageMap.json", docName)), mapping.toStringPretty(), new OpenOption[0]);
            return parsedDoc;
        }
        catch (final Exception e) {
            System.out.println("Failed to parse  " + e.getMessage());
            throw e;
        }
    }
    
    public static String TABS(final int tabs) {
        String TABS = "";
        for (int i = 0; i < tabs; ++i) {
            TABS = TABS;
        }
        return TABS;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankParser.class);
    }
}
