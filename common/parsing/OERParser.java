// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import org.jdbi.v3.core.Handle;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import com.upthinkexperts.common.domain.oer.OERSegment;
import com.yojito.minima.api.Context;
import java.util.Properties;
import com.upthinkexperts.common.domain.oer.OERDocument;
import com.upthinkexperts.minima.db.Database;
import com.upthinkexperts.common.upload.oer.OERSegments;
import java.util.UUID;
import com.upthinkexperts.Bootstrap;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import com.yojito.minima.gson.GsonObject;
import java.nio.file.Paths;
import com.upthinkexperts.common.util.ParsedDocUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import com.upthinkexperts.common.parsing.debug.ParseNodeTree;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.InputStream;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import java.util.regex.Pattern;
import com.yojito.minima.logging.MinimaLogger;

public class OERParser
{
    private static final MinimaLogger LOGGER;
    private static final Pattern tagPattern;
    private static final Pattern attValueAll;
    private static final Pattern attributeValue;
    
    public static ParsedDoc parse(final String docName, final ParsingSpec parsingSpec, final InputStream fis, final String equationTempPath, final String tempDirpath) throws IOException {
        final XWPFDocument document = new XWPFDocument(fis);
        final DocxRelationshipProcessor relationshipProcessor = new DocxRelationshipProcessor(ParsingSpec.DocFormat.OER, document, equationTempPath, tempDirpath);
        relationshipProcessor.process(docName, true);
        final ParsedDoc parsedDoc = new ParsedDoc(docName, new ArrayList<DocPart>(), relationshipProcessor.getDocImages());
        final Iterator<IBodyElement> bodyElementIterator = document.getBodyElementsIterator();
        final int defaultFontSize = document.getStyles().getDefaultRunStyle().getFontSize();
        int bodyElementIndex = 0;
        final Map<IBodyElement, Integer> elementOrderMap = new HashMap<IBodyElement, Integer>();
        final Map<Integer, IBodyElement> elementReverseOrderMap = new HashMap<Integer, IBodyElement>();
        while (bodyElementIterator.hasNext()) {
            final IBodyElement element = bodyElementIterator.next();
            elementOrderMap.put(element, bodyElementIndex);
            elementReverseOrderMap.put(bodyElementIndex++, element);
            OERParser.LOGGER.debug("BodyElementIndex - %s -> %d ", new Object[] { element.getElementType().name(), bodyElementIndex - 1 });
        }
        final boolean traceLoggingEnabled = OERParser.LOGGER.isTraceEnabled();
        final DocPart root = new DocPart(null, DocPartTypes.START, new ArrayList<DocPart.DocRun>());
        parsedDoc.add(root);
        DocPart current = root;
        int tabs = 0;
        final List<XWPFParagraph> paragraphs = document.getParagraphs();
        final AtomicInteger atomicInteger = new AtomicInteger();
        ParseNodeTree currentDebugTree;
        final ParseNodeTree ROOT = currentDebugTree = new ParseNodeTree("ROOT", null);
        try {
            for (final XWPFParagraph paragraph : paragraphs) {
                final Integer paragraphIndex = elementOrderMap.get(paragraph);
                OERParser.LOGGER.debug("paragraphIndex -> %d, currentIndex -> %d", new Object[] { paragraphIndex, atomicInteger.get() });
                while (paragraphIndex > atomicInteger.get()) {
                    OERParser.LOGGER.debug("Need to add TABLE with index = %d", new Object[] { atomicInteger.get() });
                    final XWPFTable poiTable = (XWPFTable)elementReverseOrderMap.get(atomicInteger.get());
                    PoiDocParser.addTable(poiTable, current, document, relationshipProcessor);
                    atomicInteger.incrementAndGet();
                }
                current.startParagraph();
                final List<DocPart.DocRun> list = PoiDocParser.paragraphToDocRuns((IBodyElement)paragraph, relationshipProcessor, defaultFontSize);
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
                            final CompositeDetails composition = parsingSpec.checkComposite(paragraphText, null);
                            OERParser.LOGGER.debug("isComposite : %s : %b", new Object[] { paragraphText, composition.isComposite() });
                            if (composition.isComposite()) {
                                final DocPartTypes type = composition.getDocPartTypes();
                                final String text = composition.getText();
                                if (parsingSpec.checkHierarchy(current.getType(), type)) {
                                    final Map<String, String> attributeMap = getTagAttributes(text);
                                    currentDebugTree = currentDebugTree.createChild(type.types.toString());
                                    final DocPart newChild = current.child(type, attributeMap);
                                    newChild.addTextRun(text, docRun.isBold(), docRun.isItalic(), docRun.getUnderline(), docRun.getFontName(), docRun.getFontColor(), docRun.getFontSize());
                                    newChild.end();
                                    continue;
                                }
                                throw new RuntimeException(String.format("Wrong hierarchy : Either tags are not closed (probably due to spelling mistake or you forgot to add closing tag) : Child Type %s, Parent Type %s, Node: %s", type, current.getType(), current.toString()));
                            }
                            else {
                                final boolean isMarker = parsingSpec.isMarker(paragraphText);
                                if (isMarker) {
                                    final boolean isStart = parsingSpec.isMarkerStart(paragraphText);
                                    final boolean isEnd = parsingSpec.isMarkerEnd(paragraphText);
                                    if (isStart) {
                                        final DocPartTypes type2 = DocPartTypes.getType(paragraphText);
                                        if (traceLoggingEnabled) {
                                            OERParser.LOGGER.trace("%sSTART %s---------------------------------------------------------------------\n", new Object[] { QBankParser.TABS(tabs), type2.types });
                                            OERParser.LOGGER.trace("CURRENT.TYPE = %s, TYPE = %s", new Object[] { current.getType(), type2 });
                                        }
                                        if (!parsingSpec.checkHierarchy(current.getType(), type2)) {
                                            OERParser.LOGGER.warn("Hierarchy Error Current Tree - %s", new Object[] { currentDebugTree.toStringPretty() });
                                            OERParser.LOGGER.warn("Hierarchy Error ROOT Tree - %s", new Object[] { ROOT.toStringPretty() });
                                            throw new RuntimeException(String.format("Wrong hierarchy : Either tags are not closed (probably due to spelling mistake or you forgot to add closing tag) : Child Type %s, Parent Type %s", type2, current.getType()));
                                        }
                                        final Map<String, String> attributeMap2 = getTagAttributes(paragraphText);
                                        currentDebugTree = currentDebugTree.createChild(type2.types.toString());
                                        current = current.child(type2, attributeMap2);
                                        ++tabs;
                                    }
                                    else if (isEnd) {
                                        final DocPartTypes type2 = DocPartTypes.getType(paragraphText);
                                        --tabs;
                                        if (traceLoggingEnabled) {
                                            OERParser.LOGGER.trace("%sEND %s---------------------------------------------------------------------\n", new Object[] { QBankParser.TABS(tabs), type2.types });
                                            OERParser.LOGGER.trace("END CURRENT.TYPE %s", new Object[] { current.getType() });
                                        }
                                        current.end();
                                        current = current.getParent();
                                        currentDebugTree = currentDebugTree.getParent();
                                    }
                                }
                                if (paragraphText == null || isMarker) {
                                    continue;
                                }
                                current.addTextRun(ParsedDocUtil.PTrim(paragraphText), docRun.isBold(), docRun.isItalic(), docRun.getUnderline(), docRun.getFontName(), docRun.getFontColor(), docRun.getFontSize());
                                if (traceLoggingEnabled) {
                                    OERParser.LOGGER.trace("%s %d -> [%s]", new Object[] { QBankParser.TABS(tabs), current.paragraphs.size(), paragraphText });
                                    continue;
                                }
                                continue;
                            }
                            break;
                        }
                    }
                }
                current.endParagraph();
                OERParser.LOGGER.debug("\n\n", new Object[0]);
                atomicInteger.incrementAndGet();
            }
            final File rootDirectory = Paths.get(tempDirpath, new String[0]).getParent().toFile();
            final GsonObject mapping = new GsonObject();
            relationshipProcessor.getOleToImageEqMap().forEach((o, i) -> mapping.put(o, i));
            Files.writeString(Paths.get(rootDirectory.getAbsolutePath(), String.format("%s-oleImageMap.json", docName)), mapping.toStringPretty(), new OpenOption[0]);
            return parsedDoc;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static void main(final String[] args) throws FileNotFoundException {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/openstax/intermediate-algebra-2e/intermediate-algebra-2e/";
        final String docId = "algebra-2e-7-1-multiply-and-divide-rational-expressions";
        final String fileName = "7-1-multiply-and-divide-rational-expressions-tagged.docx";
        final String tempPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/olebin/" + docId;
        final String imagesPathPrefix = "/work/myworkspace/druta/projects/upthinkexperts/dump/images/" + docId;
        final FileInputStream fis = new FileInputStream(new File(directory, fileName));
        try {
            final OERParser docParser = new OERParser();
            final ParsedDoc parsedDoc = parse(docId, ParsingSpecs.OPENSTAX, fis, tempPathPrefix, imagesPathPrefix);
            final OERDocConverter converter = new OERDocConverter();
            final OERDocument document = converter.convert(null, parsedDoc, "");
            System.out.printf("OERDocument - \n %s", document.toStringPretty());
            final Properties properties = Bootstrap.readAndPrintProperties("Starting CMS Server for [ %S ] ENVIRONMENT", args);
            final Context context = Bootstrap.buildContext(properties);
            Database.doWithinTx(context.getDatabase(), handle -> {
                final List<OERSegment> list = OERSegments.createAndReturnSegments(document, "YOJITO", handle, UUID.randomUUID().toString());
                System.out.printf("Segment List - %s", list);
            });
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println(" Error for [" + fileName + "] -" + e.getMessage());
        }
    }
    
    public static Map<String, String> getTagAttributes(final String tagText) {
        Matcher m = OERParser.tagPattern.matcher(tagText);
        final boolean tagFound = m.find();
        if (!tagFound) {
            return null;
        }
        final String attributes = m.group(2);
        OERParser.LOGGER.debug("getTagAttributes %s :: Attributes : %s", new Object[] { tagText, attributes });
        if (attributes.length() > 0) {
            final Map<String, String> attributeMap = new HashMap<String, String>();
            m = OERParser.attValueAll.matcher(attributes);
            while (m.find()) {
                final String attrValue = m.group(0);
                OERParser.LOGGER.debug("getTagAttributes attributeValue :: Attribute :  >> [" + attrValue, new Object[0]);
                final Matcher am = OERParser.attributeValue.matcher(attrValue);
                if (am.find()) {
                    final String aName = am.group(1);
                    final String aVal = am.group(2);
                    OERParser.LOGGER.debug("getTagAttributes attributeValue :: Match : %s -> [%s]", new Object[] { aName, aVal });
                    attributeMap.put(aName, aVal);
                }
            }
            return attributeMap;
        }
        return null;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)OERParser.class);
        tagPattern = Pattern.compile("<(\\S+)(.*?)>");
        attValueAll = Pattern.compile("([\\w:\\-]+)(\\s*=\\s*(\"(.*?)\"|'(.*?)'|([^ ]*))|(\\s+|\\z))");
        attributeValue = Pattern.compile("(\\S+)=\"(.+)\"");
    }
}
