// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Optional;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import java.util.Map;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import com.yojito.minima.gson.GsonObject;
import java.nio.file.Paths;
import com.upthinkexperts.common.util.ParsedDocUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import com.yojito.minima.logging.MinimaLogger;

public class PoiDocParser implements DocParser
{
    private static final MinimaLogger LOGGER;
    
    private static String TABS(final int tabs) {
        String TABS = "";
        for (int i = 0; i < tabs; ++i) {
            TABS = TABS;
        }
        return TABS;
    }
    
    @Override
    public ParsedDoc parse(final String docId, final boolean isQuestion, final ParsingSpec parsingSpec, final File file, final String equationTempPath, final String tempDirpath) throws IOException {
        final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        return this.parse(docId, isQuestion, parsingSpec, fis, equationTempPath, tempDirpath);
    }
    
    @Override
    public ParsedDoc parse(final String docId, final boolean isQuestion, final ParsingSpec parsingSpec, final InputStream fis, final String equationTempPath, final String tempDirpath) throws IOException {
        final XWPFDocument document = new XWPFDocument(fis);
        final DocxRelationshipProcessor relationshipProcessor = new DocxRelationshipProcessor(ParsingSpec.DocFormat.CMS, document, equationTempPath, tempDirpath);
        relationshipProcessor.process(docId, isQuestion);
        final ParsedDoc parsedDoc = new ParsedDoc(docId, new ArrayList<DocPart>(), relationshipProcessor.getDocImages());
        final DocPart root = new DocPart(null, DocPartTypes.START, new ArrayList<DocPart.DocRun>());
        parsedDoc.add(root);
        DocPart current = root;
        int tabs = 0;
        final Iterator<IBodyElement> bodyElementIterator = document.getBodyElementsIterator();
        final int defaultFontSize = document.getStyles().getDefaultRunStyle().getFontSize();
        int bodyElementIndex = 0;
        final Map<IBodyElement, Integer> elementOrderMap = new HashMap<IBodyElement, Integer>();
        final Map<Integer, IBodyElement> elementReverseOrderMap = new HashMap<Integer, IBodyElement>();
        while (bodyElementIterator.hasNext()) {
            final IBodyElement element = bodyElementIterator.next();
            elementOrderMap.put(element, bodyElementIndex);
            elementReverseOrderMap.put(bodyElementIndex++, element);
            PoiDocParser.LOGGER.debug("BodyElementIndex - %s -> %d ", new Object[] { element.getElementType().name(), bodyElementIndex - 1 });
        }
        final List<XWPFParagraph> paragraphs = document.getParagraphs();
        final AtomicInteger atomicInteger = new AtomicInteger();
        final boolean traceLoggingEnabled = PoiDocParser.LOGGER.isTraceEnabled();
        try {
            for (final XWPFParagraph paragraph : paragraphs) {
                final Integer paragraphIndex = elementOrderMap.get(paragraph);
                PoiDocParser.LOGGER.debug("paragraphIndex -> %d, currentIndex -> %d", new Object[] { paragraphIndex, atomicInteger.get() });
                while (paragraphIndex > atomicInteger.get()) {
                    PoiDocParser.LOGGER.debug("Need to add TABLE with index = %d", new Object[] { atomicInteger.get() });
                    final XWPFTable poiTable = (XWPFTable)elementReverseOrderMap.get(atomicInteger.get());
                    addTable(poiTable, current, document, relationshipProcessor);
                    atomicInteger.incrementAndGet();
                }
                current.startParagraph();
                final List<DocPart.DocRun> list = paragraphToDocRuns((IBodyElement)paragraph, relationshipProcessor, defaultFontSize);
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
                        case TEXT: {
                            final String paragraphText = docRun.getText();
                            final boolean isMarker = parsingSpec.isMarker(paragraphText);
                            PoiDocParser.LOGGER.trace("[%s] - isMarker : %b", new Object[] { paragraphText, isMarker });
                            if (isMarker) {
                                final boolean isStart = parsingSpec.isMarkerStart(paragraphText);
                                final boolean isEnd = parsingSpec.isMarkerEnd(paragraphText);
                                if (isStart) {
                                    final DocPartTypes type = DocPartTypes.getType(paragraphText);
                                    if (traceLoggingEnabled) {
                                        PoiDocParser.LOGGER.trace("%sSTART %s---------------------------------------------------------------------\n", new Object[] { TABS(tabs), type.types });
                                    }
                                    current = current.child(type);
                                    ++tabs;
                                }
                                else if (isEnd) {
                                    final DocPartTypes type = DocPartTypes.getType(paragraphText);
                                    --tabs;
                                    if (traceLoggingEnabled) {
                                        PoiDocParser.LOGGER.trace("%sEND %s---------------------------------------------------------------------\n", new Object[] { TABS(tabs), type.types });
                                    }
                                    current.end();
                                    current = current.getParent();
                                }
                            }
                            if (paragraphText == null || isMarker) {
                                continue;
                            }
                            current.addTextRun(ParsedDocUtil.PTrim(paragraphText), docRun.isBold(), docRun.isItalic(), docRun.getUnderline(), docRun.getFontName(), docRun.getFontColor(), docRun.getFontSize());
                            if (traceLoggingEnabled) {
                                PoiDocParser.LOGGER.trace("%s %d -> %s", new Object[] { TABS(tabs), current.paragraphs.size(), paragraphText });
                                continue;
                            }
                            continue;
                        }
                    }
                }
                current.endParagraph();
                if (traceLoggingEnabled) {
                    PoiDocParser.LOGGER.trace("\n\n", new Object[0]);
                }
                atomicInteger.incrementAndGet();
            }
            final File rootDirectory = Paths.get(tempDirpath, new String[0]).getParent().toFile();
            final GsonObject mapping = new GsonObject();
            relationshipProcessor.getOleToImageEqMap().forEach((o, i) -> mapping.put(o, i));
            Files.writeString(Paths.get(rootDirectory.getAbsolutePath(), String.format("%s-%s-oleImageMap.json", docId, isQuestion ? "Q" : "A")), mapping.toStringPretty(), new OpenOption[0]);
            return parsedDoc;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static void addTable(final XWPFTable table, final DocPart current, final XWPFDocument document, final DocxRelationshipProcessor relationshipProcessor) {
        current.startParagraph();
        final DocPart.DocTable docTable = DocXTableReader.readTable(table, document, relationshipProcessor);
        current.addTableRun(new DocPart.DocRun(DocPart.DocRunType.TABLE, docTable));
        current.endParagraph();
    }
    
    public static void main(final String[] args) {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/openstax/intermediate-algebra-2e/intermediate-algebra-2e/";
        final String docId = "algebra-2e-8-7-use-radicals-in-function";
        final String fileName = "8-7-use-radicals-in-functions -tagged.docx";
        final String tempPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/olebin/" + docId;
        final String imagesPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/images/" + docId;
        try {
            final PoiDocParser docParser = new PoiDocParser();
            final ParsedDoc parsedDoc = docParser.parse(docId, false, ParsingSpecs.OPENSTAX, new File(directory, fileName), tempPathPrefix, imagesPathPrefix);
            System.out.println(parsedDoc);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("P Error for [" + fileName + "] -" + e.getMessage());
        }
    }
    
    public static List<DocPart.DocRun> paragraphToDocRuns(final IBodyElement element, final DocxRelationshipProcessor relationshipProcessor, final int defaultFontSize) {
        final List<DocPart.DocRun> list = new ArrayList<DocPart.DocRun>();
        if (element instanceof XWPFParagraph) {
            final XWPFParagraph paragraph = (XWPFParagraph)element;
            final XmlCursor xmlCursor = paragraph.getCTP().newCursor();
            boolean isComposite = false;
            TextStyle textStyle = new TextStyle();
            StringBuilder textContent = new StringBuilder();
            while (xmlCursor.hasNextToken()) {
                final XmlCursor.TokenType tokenType = xmlCursor.toNextToken();
                if (tokenType.isStart()) {
                    final XmlObject xmlObject = xmlCursor.getObject();
                    final String localPart = xmlCursor.getName().getLocalPart();
                    if (localPart.equalsIgnoreCase("object")) {
                        System.out.println("=========> object <=========");
                        System.out.println("xmlObject-object -> " + xmlObject);
                    }
                    if (localPart.equalsIgnoreCase("pic")) {
                        try {
                            final Optional<String> imageName = relationshipProcessor.getImageName(xmlObject);
                            if (imageName.isPresent()) {
                                if (textContent.length() != 0) {
                                    list.add(new DocPart.DocRun(textContent.toString(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getFontFamily(), textStyle.getColor(), textStyle.getFontSize()));
                                    textContent = new StringBuilder();
                                }
                                PoiDocParser.LOGGER.debug("imageName -> [%s]", new Object[] { imageName.get() });
                                list.add(new DocPart.DocRun(DocPart.DocRunType.IMAGE, imageName.get()));
                            }
                        }
                        catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (xmlCursor.getTextValue().isEmpty()) {
                        continue;
                    }
                    isComposite = ParsingSpecs.QBANK.isMarker(paragraph.getText());
                    if (xmlCursor.getName().getPrefix().equalsIgnoreCase("w") && localPart.equalsIgnoreCase("r")) {
                        try {
                            textStyle = DocxRelationshipProcessor.getAllStyles(xmlObject);
                        }
                        catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (textStyle.getIsImage()) {
                            PoiDocParser.LOGGER.debug("%s -> it is an image", new Object[] { xmlCursor.getTextValue() });
                        }
                        else {
                            textContent.append(textStyle.getText());
                            if (isComposite) {
                                continue;
                            }
                            list.add(new DocPart.DocRun(textContent.toString(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getFontFamily(), textStyle.getColor(), textStyle.getFontSize()));
                            textContent = new StringBuilder();
                        }
                    }
                    else {
                        if (!localPart.equalsIgnoreCase("oMath")) {
                            continue;
                        }
                        list.add(new DocPart.DocRun(textContent.toString(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getFontFamily(), textStyle.getColor(), textStyle.getFontSize()));
                        textContent = new StringBuilder();
                        try {
                            textStyle = DocxRelationshipProcessor.getAllStyles(xmlObject);
                        }
                        catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                        list.add(new DocPart.DocRun(DocPart.DocRunType.EQUATION, textStyle.getText(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getFontFamily(), textStyle.getColor(), textStyle.getFontSize(), true));
                    }
                }
                else {
                    if (!tokenType.isEnd()) {
                        continue;
                    }
                    xmlCursor.push();
                    xmlCursor.toParent();
                    if (xmlCursor.getName().getLocalPart().equalsIgnoreCase("p")) {
                        break;
                    }
                    xmlCursor.pop();
                }
            }
            list.add(new DocPart.DocRun(textContent.toString(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getFontFamily(), textStyle.getColor(), textStyle.getFontSize()));
        }
        if (element instanceof XWPFTable) {
            final XWPFTable table = (XWPFTable)element;
            final List<XWPFTableRow> rows = table.getRows();
            final List<DocPart.DocTableRow> tableRows = new ArrayList<DocPart.DocTableRow>();
            List<DocPart.DocTableCell> tableCells = new ArrayList<DocPart.DocTableCell>();
            List<DocPart.DocRun> tableCellData = new ArrayList<DocPart.DocRun>();
            for (final XWPFTableRow row : rows) {
                final List<XWPFTableCell> cells = row.getTableCells();
                for (final XWPFTableCell cell : cells) {
                    final List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    for (final XWPFParagraph paragraph2 : paragraphs) {
                        final XmlCursor xmlCursor2 = paragraph2.getCTP().newCursor();
                        while (xmlCursor2.hasNextToken()) {
                            final XmlCursor.TokenType tokenType2 = xmlCursor2.toNextToken();
                            if (tokenType2.isStart()) {
                                final XmlObject xmlObject2 = xmlCursor2.getObject();
                                final String localPart2 = xmlCursor2.getName().getLocalPart();
                                if (xmlCursor2.getTextValue().isEmpty()) {
                                    continue;
                                }
                                if (xmlCursor2.getName().getPrefix().equalsIgnoreCase("w") && localPart2.equalsIgnoreCase("r")) {
                                    TextStyle textStyle2;
                                    try {
                                        textStyle2 = DocxRelationshipProcessor.getAllStyles(xmlObject2);
                                    }
                                    catch (final Exception e2) {
                                        throw new RuntimeException(e2);
                                    }
                                    tableCellData.add(new DocPart.DocRun(textStyle2.getText(), textStyle2.getIsBold(), textStyle2.getIsItalic(), textStyle2.getUnderline(), textStyle2.getFontFamily(), textStyle2.getColor(), textStyle2.getFontSize()));
                                }
                                else {
                                    if (!localPart2.equalsIgnoreCase("oMath")) {
                                        continue;
                                    }
                                    TextStyle textStyle2;
                                    try {
                                        textStyle2 = DocxRelationshipProcessor.getAllStyles(xmlObject2);
                                    }
                                    catch (final Exception e2) {
                                        throw new RuntimeException(e2);
                                    }
                                    tableCellData.add(new DocPart.DocRun(DocPart.DocRunType.EQUATION, textStyle2.getText(), textStyle2.getIsBold(), textStyle2.getIsItalic(), textStyle2.getUnderline(), textStyle2.getFontFamily(), textStyle2.getColor(), textStyle2.getFontSize(), true));
                                }
                            }
                            else {
                                if (!tokenType2.isEnd()) {
                                    continue;
                                }
                                xmlCursor2.push();
                                xmlCursor2.toParent();
                                if (xmlCursor2.getName().getLocalPart().equalsIgnoreCase("p")) {
                                    break;
                                }
                                xmlCursor2.pop();
                            }
                        }
                    }
                    tableCells.add(new DocPart.DocTableCell(tableCellData));
                    tableCellData = new ArrayList<DocPart.DocRun>();
                }
                tableRows.add(new DocPart.DocTableRow(tableCells));
                tableCells = new ArrayList<DocPart.DocTableCell>();
            }
            list.add(new DocPart.DocRun(DocPart.DocRunType.TABLE, new DocPart.DocTable(tableRows)));
        }
        return list;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)PoiDocParser.class);
    }
}
