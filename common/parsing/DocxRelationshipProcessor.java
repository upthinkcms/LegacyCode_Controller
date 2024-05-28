// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Collection;
import java.util.HashSet;
import java.nio.file.Paths;
import com.yojito.minima.gson.GsonArray;
import java.io.IOException;
import java.util.Base64;
import java.nio.file.Files;
import com.yojito.minima.gson.GsonObject;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.picture.impl.CTPictureImpl;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTTextImpl;
import java.util.Arrays;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import com.upthinkexperts.common.mathml.WordReadFormulas;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import java.util.Iterator;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTOMathParaImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTR;
import org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTOMathImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTRImpl;
import org.apache.xmlbeans.XmlObject;
import java.io.OutputStream;
import java.io.InputStream;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import java.util.List;
import java.util.Set;
import com.yojito.minima.logging.MinimaLogger;

public class DocxRelationshipProcessor
{
    public static final String IMAGES = "images";
    public static final String IMAGES2 = "images2";
    private static final MinimaLogger LOGGER;
    private static final String OLE_TYPE = "application/vnd.openxmlformats-officedocument.oleObject";
    private static final String declareNameSpaces = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' ";
    private static final String OBJECT_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object";
    private static final String OLE_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object/o:OLEObject/@r:id";
    private static final String IMAGE_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object/v:shape/v:imagedata/@r:id";
    private static final String EMBEDDED_IMAGE_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill/a:blip/@r:embed";
    private static final String EMBEDDED_IMAGE_ROOT_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill";
    private static final String XWPF_PARA_ROOT_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .";
    private static final String XWPF_PARA_ROOT_CHILD_PATH = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//*";
    private static final Set<String> imageKnownFormats;
    List<XWPFPictureData> pictureDataList;
    List<POIXMLDocumentPart> documentPartList;
    List<POIXMLDocumentPart> oleList;
    Map<String, POIXMLDocumentPart> oleMap;
    Map<String, String> imageIdToOleIdMap;
    List<String> docImages;
    Map<String, String> oleToImageEqMap;
    private final XWPFDocument document;
    private final String oleBinOutputDir;
    private final String imageOutputDir;
    private final ParsingSpec.DocFormat docFormat;
    
    public DocxRelationshipProcessor(final ParsingSpec.DocFormat docFormat, final XWPFDocument document, final String oleBinOutputDir, final String imageOutputDir) {
        this.pictureDataList = new ArrayList<XWPFPictureData>();
        this.documentPartList = new ArrayList<POIXMLDocumentPart>();
        this.oleList = new ArrayList<POIXMLDocumentPart>();
        this.oleMap = new HashMap<String, POIXMLDocumentPart>();
        this.imageIdToOleIdMap = new HashMap<String, String>();
        this.docImages = new ArrayList<String>();
        this.oleToImageEqMap = new HashMap<String, String>();
        this.document = document;
        this.oleBinOutputDir = oleBinOutputDir;
        this.imageOutputDir = imageOutputDir;
        this.docFormat = docFormat;
    }
    
    public void process(final String docId, final boolean isQuestion) {
        final boolean traceLoggingEnabled = DocxRelationshipProcessor.LOGGER.isTraceEnabled();
        final boolean debugLoggingEnabled = DocxRelationshipProcessor.LOGGER.isDebugEnabled();
        this.document.getRelations().forEach(relation -> {
            try {
                final POIXMLDocumentPart documentPart = relation;
                if (debugLoggingEnabled) {
                    DocxRelationshipProcessor.LOGGER.debug("Relation %s CT=%s", new Object[] { relation, relation.getPackagePart().getContentType() });
                }
                if (relation instanceof XWPFPictureData) {
                    this.pictureDataList.add((XWPFPictureData)relation);
                    if (debugLoggingEnabled) {
                        DocxRelationshipProcessor.LOGGER.debug("Found image = %s %s", new Object[] { relation.getPackagePart().getPartName().getName(), relation.getPackagePart().getContentType() });
                    }
                    if (DocxRelationshipProcessor.imageKnownFormats.contains(relation.getPackagePart().getContentType())) {
                        final String imageName = relation.getPackagePart().getPartName().getName().substring("/word/media/".length());
                        if (debugLoggingEnabled) {
                            DocxRelationshipProcessor.LOGGER.debug("Adding image = %s to oleMap", new Object[] { imageName });
                        }
                        String oleFileName = null;
                        switch (this.docFormat) {
                            case OER:
                            case QBANK: {
                                oleFileName = String.format("%s-%s", docId, imageName);
                                break;
                            }
                            case CMS: {
                                oleFileName = String.format("%s-%s-%s", docId, isQuestion ? "Q" : "A", imageName);
                                break;
                            }
                        }
                        relation.getPackagePart().getInputStream();
                        new FileOutputStream(new File(this.imageOutputDir, oleFileName));
                        final FileOutputStream fileOutputStream;
                        final Object o;
                        IOUtils.copy((InputStream)o, (OutputStream)fileOutputStream);
                        this.docImages.add(imageName);
                    }
                }
                else if (relation instanceof POIXMLDocumentPart) {
                    this.documentPartList.add(relation);
                    if (relation.getPackagePart().getContentType().equals("application/vnd.openxmlformats-officedocument.oleObject")) {
                        final String oleName = relation.getPackagePart().getPartName().getName().substring("/word/embeddings/".length());
                        if (debugLoggingEnabled) {
                            DocxRelationshipProcessor.LOGGER.debug("Adding ole = %s to oleMap", new Object[] { oleName });
                        }
                        String oleFileName2 = null;
                        switch (this.docFormat) {
                            case OER:
                            case QBANK: {
                                oleFileName2 = String.format("%s-%s", docId, oleName);
                                break;
                            }
                            case CMS: {
                                oleFileName2 = String.format("%s-%s-%s", docId, isQuestion ? "Q" : "A", oleName);
                                break;
                            }
                        }
                        relation.getPackagePart().getInputStream();
                        new FileOutputStream(new File(this.oleBinOutputDir, oleFileName2));
                        final FileOutputStream fileOutputStream2;
                        final Object o2;
                        IOUtils.copy((InputStream)o2, (OutputStream)fileOutputStream2);
                        this.oleMap.put(oleName, relation);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }
    
    public static TextStyle getAllStyles(final XmlObject xmlObject) throws Exception {
        TextStyle textStyle = new TextStyle();
        boolean isImage = false;
        String textValue = null;
        if (xmlObject instanceof CTRImpl) {
            final CTRImpl ctr = (CTRImpl)xmlObject;
            textValue = ctr.getStringValue();
            final List<CTDrawing> ctDrawingList = ctr.getDrawingList();
            if (ctDrawingList != null) {
                for (final CTDrawing ctDrawing : ctDrawingList) {
                    if (ctDrawing != null) {
                        isImage = true;
                    }
                }
            }
            final CTRPr ctrPr = ctr.getRPr();
            TextStyle tempTextStyle = new TextStyle();
            if (ctrPr != null) {
                tempTextStyle = getTextStyle(ctrPr);
            }
            textStyle = new TextStyle(tempTextStyle.getColor(), tempTextStyle.getFontFamily(), tempTextStyle.getFontSize(), tempTextStyle.getIsBold(), tempTextStyle.getIsItalic(), tempTextStyle.getUnderline(), isImage, textValue);
        }
        else if (xmlObject instanceof org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTRImpl) {
            final org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTRImpl ctr2 = (org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTRImpl)xmlObject;
            textValue = ctr2.getStringValue();
            final CTRPr ctrPr2 = ctr2.getRPr2();
            TextStyle tempTextStyle2 = new TextStyle();
            if (ctrPr2 != null) {
                tempTextStyle2 = getTextStyle(ctrPr2);
            }
            textStyle = new TextStyle(tempTextStyle2.getColor(), tempTextStyle2.getFontFamily(), tempTextStyle2.getFontSize(), tempTextStyle2.getIsBold(), tempTextStyle2.getIsItalic(), tempTextStyle2.getUnderline(), isImage, textValue);
        }
        else if (xmlObject instanceof CTOMathImpl) {
            textValue = getMathMLString(xmlObject);
            final CTOMathImpl ctoMath = (CTOMathImpl)xmlObject;
            final List<CTR> ctrList = ctoMath.getRList();
            TextStyle tempTextStyle2 = new TextStyle();
            for (final CTR ctr3 : ctrList) {
                final CTRPr ctrPr3 = ctr3.getRPr2();
                if (ctrPr3 != null) {
                    tempTextStyle2 = getTextStyle(ctrPr3);
                }
            }
            textStyle = new TextStyle(tempTextStyle2.getColor(), tempTextStyle2.getFontFamily(), tempTextStyle2.getFontSize(), tempTextStyle2.getIsBold(), tempTextStyle2.getIsItalic(), tempTextStyle2.getUnderline(), isImage, textValue);
        }
        else if (xmlObject instanceof CTOMathParaImpl) {
            textValue = getMathMLString(xmlObject);
            final CTOMathParaImpl ctoMathPara = (CTOMathParaImpl)xmlObject;
            final List<CTOMath> ctoMathList = ctoMathPara.getOMathList();
            for (final CTOMath ctoMath2 : ctoMathList) {
                final List<CTR> ctrList2 = ctoMath2.getRList();
                for (final CTR ctr4 : ctrList2) {
                    final CTRPr ctrPr4 = ctr4.getRPr2();
                    TextStyle tempTextStyle3 = new TextStyle();
                    if (ctrPr4 != null) {
                        tempTextStyle3 = getTextStyle(ctrPr4);
                    }
                    textStyle = new TextStyle(tempTextStyle3.getColor(), tempTextStyle3.getFontFamily(), tempTextStyle3.getFontSize(), tempTextStyle3.getIsBold(), tempTextStyle3.getIsItalic(), tempTextStyle3.getUnderline(), isImage, textValue);
                }
            }
        }
        System.out.printf("// TextStyle -> text: [%s], color: %s, fontFamily: %s, fontSize: %d, isBold: %b, isItalic: %b, underline: %s, isImage: %b\n", textStyle.getText(), textStyle.getColor(), textStyle.getFontFamily(), textStyle.getFontSize(), textStyle.getIsBold(), textStyle.getIsItalic(), textStyle.getUnderline(), textStyle.getIsImage());
        return textStyle;
    }
    
    public static TextStyle getTextStyle(final CTRPr ctrPr) {
        String color = null;
        String fontFamily = null;
        int fontSize = -1;
        boolean isBold = false;
        boolean isItalic = false;
        String underline = null;
        final List<CTFonts> fonts = ctrPr.getRFontsList();
        for (final CTFonts font : fonts) {
            if (font != null) {
                fontFamily = font.getAscii();
                break;
            }
        }
        final List<CTOnOff> ctOnOffBs = ctrPr.getBList();
        for (final CTOnOff ctOnOffB : ctOnOffBs) {
            if (ctOnOffB != null) {
                final STOnOff stOnOff = ctOnOffB.xgetVal();
                if (stOnOff == null) {
                    isBold = true;
                    break;
                }
                if (stOnOff.getStringValue().equals("true")) {
                    isBold = true;
                    break;
                }
                break;
            }
        }
        final List<CTOnOff> ctOnOffIs = ctrPr.getIList();
        for (final CTOnOff ctOnOffI : ctOnOffIs) {
            if (ctOnOffI != null) {
                final STOnOff stOnOff2 = ctOnOffI.xgetVal();
                if (stOnOff2 == null) {
                    isItalic = true;
                    break;
                }
                if (stOnOff2.getStringValue().equals("true")) {
                    isItalic = true;
                    break;
                }
                break;
            }
        }
        final List<CTHpsMeasure> ctHpsMeasures = ctrPr.getSzList();
        for (final CTHpsMeasure ctHpsMeasure : ctHpsMeasures) {
            if (ctHpsMeasure != null) {
                fontSize = Integer.parseInt(ctHpsMeasure.getVal().toString());
                break;
            }
        }
        final List<CTColor> ctColors = ctrPr.getColorList();
        for (final CTColor ctColor : ctColors) {
            if (ctColor != null) {
                final STHexColor stHexColor = ctColor.xgetVal();
                color = stHexColor.getStringValue();
                break;
            }
        }
        final List<CTUnderline> ctUnderlines = ctrPr.getUList();
        for (final CTUnderline ctUnderline : ctUnderlines) {
            if (ctUnderline != null) {
                final STUnderline stUnderline = ctUnderline.xgetVal();
                if (stUnderline != null) {
                    underline = stUnderline.getStringValue();
                    break;
                }
                continue;
            }
        }
        return new TextStyle(color, fontFamily, fontSize, isBold, isItalic, underline);
    }
    
    public static String getMathMLString(final XmlObject xmlObject) throws Exception {
        return WordReadFormulas.getMathML((CTOMath)xmlObject);
    }
    
    public List<Pair<Integer, String>> pokeForOfficeMath(final XWPFParagraph paragraph) throws Exception {
        final boolean traceLoggingEnabled = DocxRelationshipProcessor.LOGGER.isTraceEnabled();
        final boolean debugLoggingEnabled = DocxRelationshipProcessor.LOGGER.isDebugEnabled();
        final List<Pair<Integer, String>> mathPairs = new ArrayList<Pair<Integer, String>>();
        final List<CTOMath> mathList = paragraph.getCTP().getOMathList();
        final List<CTOMathPara> mathParaList = paragraph.getCTP().getOMathParaList();
        if (mathParaList.size() > 0 && mathList.size() == 0) {
            if (mathParaList.size() > 1) {
                throw new RuntimeException("More than 1 MathPara..");
            }
            for (final CTOMathPara ctoMathPara : mathParaList) {
                final Iterator iterator2 = ctoMathPara.getOMathList().iterator();
                if (iterator2.hasNext()) {
                    final CTOMath ctoMath = (CTOMath)iterator2.next();
                    final CTOMathImpl mathImpl = (CTOMathImpl)ctoMath;
                    final String mathMl = WordReadFormulas.getMathML((CTOMath)mathImpl);
                    if (debugLoggingEnabled) {
                        DocxRelationshipProcessor.LOGGER.debug("CTOMathParaImpl2 >> %s", new Object[] { mathMl });
                    }
                    mathPairs.add((Pair<Integer, String>)Pair.of((Object)0, (Object)mathMl));
                    return mathPairs;
                }
            }
        }
        if (mathList != null && mathList.size() > 0) {
            final XmlObject[] selectPath;
            final XmlObject[] selectedObjects = selectPath = paragraph.getCTP().selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .");
            for (final XmlObject node : selectPath) {
                final XmlObject[] children = node.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//*");
                XmlObject c = null;
                final List<XmlObject> interestedNodes = Arrays.stream(children).filter(c -> c instanceof CTTextImpl || c instanceof CTOMathImpl || c instanceof CTOMathParaImpl).collect((Collector<? super XmlObject, ?, List<XmlObject>>)Collectors.toList());
                int ctoMathAdded = 0;
                int i = 0;
                while (i < interestedNodes.size()) {
                    c = interestedNodes.get(i);
                    if (traceLoggingEnabled) {
                        DocxRelationshipProcessor.LOGGER.trace("Class >> %s, %d", new Object[] { c.getClass(), i });
                    }
                    if (c instanceof CTOMathImpl) {
                        final CTOMathImpl mathImpl2 = (CTOMathImpl)c;
                        mathPairs.add((Pair<Integer, String>)Pair.of((Object)i, (Object)WordReadFormulas.getMathML((CTOMath)mathImpl2)));
                        ++i;
                        ++ctoMathAdded;
                    }
                    else if (c instanceof CTOMathParaImpl) {
                        final CTOMathParaImpl ctoMathPara2 = (CTOMathParaImpl)c;
                        if (ctoMathPara2.getOMathList().size() > 1) {
                            throw new RuntimeException("More than 1 MathPara..");
                        }
                        for (final CTOMath ctoMath2 : ctoMathPara2.getOMathList()) {
                            final CTOMathImpl mathImpl3 = (CTOMathImpl)ctoMath2;
                            final String mathMl2 = WordReadFormulas.getMathML((CTOMath)mathImpl3);
                            if (debugLoggingEnabled) {
                                DocxRelationshipProcessor.LOGGER.debug("CTOMathParaImpl >> %s", new Object[] { mathMl2 });
                            }
                            mathPairs.add((Pair<Integer, String>)Pair.of((Object)i, (Object)mathMl2));
                            ++i;
                            ++ctoMathAdded;
                        }
                    }
                    else if (c instanceof org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTTextImpl) {
                        if (traceLoggingEnabled) {
                            DocxRelationshipProcessor.LOGGER.trace("CTTextImpl1 >> %d, [%s]", new Object[] { i, ((org.openxmlformats.schemas.officeDocument.x2006.math.impl.CTTextImpl)c).getStringValue() });
                        }
                        ++i;
                    }
                    else {
                        if (!(c instanceof CTTextImpl)) {
                            throw new RuntimeException("Math Para Not implemened2!~!!!!");
                        }
                        if (traceLoggingEnabled) {
                            DocxRelationshipProcessor.LOGGER.trace("CTTextImpl2 >> %d, [%s]", new Object[] { i, ((CTTextImpl)c).getStringValue() });
                        }
                        ++i;
                    }
                    if (debugLoggingEnabled) {
                        DocxRelationshipProcessor.LOGGER.debug("", new Object[0]);
                    }
                }
                final String currentToString = mathPairs.stream().map(p -> String.format("%d -> %s", p.getLeft(), p.getRight())).collect((Collector<? super Object, ?, String>)Collectors.joining("\n", "[", "]"));
                if (debugLoggingEnabled) {
                    DocxRelationshipProcessor.LOGGER.debug("Math Node index is %s %d", new Object[] { currentToString, interestedNodes.size() });
                }
            }
        }
        if (debugLoggingEnabled) {
            DocxRelationshipProcessor.LOGGER.debug("Math Pairs are %s", new Object[] { mathPairs.stream().map(p -> String.format("%d -> %s", p.getLeft(), p.getRight())).collect((Collector<? super Object, ?, String>)Collectors.joining("\n", "[", "]")) });
        }
        return mathPairs;
    }
    
    public Optional<String> getImageName(final XmlObject object) throws Exception {
        final CTPictureImpl ctPicture = (CTPictureImpl)object;
        try {
            final XmlObject[] selectedObjects = ctPicture.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill");
            if (selectedObjects.length > 0) {
                final XmlObject[] selectedObjects2 = ctPicture.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill/a:blip/@r:embed");
                final String imageID = selectedObjects2[0].newCursor().getTextValue();
                final POIXMLDocumentPart imageRelation = this.document.getRelationById(imageID);
                String imageName = imageRelation.getPackagePart().getPartName().getName();
                imageName = imageName.substring("/word/media/".length());
                DocxRelationshipProcessor.LOGGER.debug("imageName: [%s] imageID: [%s]", new Object[] { imageName, imageID });
                return Optional.of(imageName);
            }
            DocxRelationshipProcessor.LOGGER.debug("No Image Object", new Object[0]);
            return Optional.empty();
        }
        catch (final Exception e) {
            DocxRelationshipProcessor.LOGGER.error((Throwable)e, "Error during getImageName", new Object[0]);
            return Optional.empty();
        }
    }
    
    public Optional<String> pokeForImage(final CTPicture ctPicture) {
        try {
            final XmlObject[] selectedObjects = ctPicture.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill");
            if (selectedObjects.length > 0) {
                final XmlObject[] selectedObjects2 = ctPicture.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//pic:blipFill/a:blip/@r:embed");
                final String imageID = selectedObjects2[0].newCursor().getTextValue();
                final POIXMLDocumentPart imageRelation = this.document.getRelationById(imageID);
                String imageName = imageRelation.getPackagePart().getPartName().getName();
                imageName = imageName.substring("/word/media/".length());
                DocxRelationshipProcessor.LOGGER.debug("\t\timageName : [%s] imageID = [%s]", new Object[] { imageName, imageID });
                return Optional.of(imageName);
            }
            DocxRelationshipProcessor.LOGGER.debug("\t\tNO IMAGE OBJECT", new Object[0]);
            return Optional.empty();
        }
        catch (final Exception e) {
            DocxRelationshipProcessor.LOGGER.error((Throwable)e, "Error during pokeForOLE_Image", new Object[0]);
            return Optional.empty();
        }
    }
    
    public Optional<Pair<String, String>> pokeForOLE_Equation(final XWPFRun run) {
        try {
            final XmlObject[] selectedObjects = run.getCTR().selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object");
            final XmlObject[] selectedObjects2 = run.getCTR().selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object/o:OLEObject/@r:id");
            final XmlObject[] selectedObjects3 = run.getCTR().selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; declare namespace pic='http://schemas.openxmlformats.org/drawingml/2006/picture'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships' declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//w:object/v:shape/v:imagedata/@r:id");
            if (selectedObjects.length > 0) {
                final String oleID = selectedObjects2[0].newCursor().getTextValue();
                final String imageID = selectedObjects3[0].newCursor().getTextValue();
                this.imageIdToOleIdMap.put(imageID, oleID);
                final POIXMLDocumentPart imageRelation = this.document.getRelationById(imageID);
                final POIXMLDocumentPart oleRelation = this.document.getRelationById(oleID);
                final String oleName = oleRelation.getPackagePart().getPartName().getName().substring("/word/embeddings/".length());
                final String imageName = imageRelation.getPackagePart().getPartName().getName().substring("/word/media/".length());
                String imageMapName = imageRelation.getPackagePart().getPartName().getName().substring("/word/media/".length());
                if (imageMapName.endsWith(".emf")) {
                    imageMapName = imageMapName.substring(0, imageMapName.length() - 4) + "-emf.png";
                }
                DocxRelationshipProcessor.LOGGER.debug("\tOLE : [%s] imageID = [%s] oleName = [%s] imageName = [%s]", new Object[] { oleID, imageID, oleName, imageName });
                this.oleToImageEqMap.put(oleName, imageName);
                return Optional.of(Pair.of((Object)oleName, (Object)imageMapName));
            }
            DocxRelationshipProcessor.LOGGER.debug("\tNO OLE OBJECT", new Object[0]);
            return Optional.empty();
        }
        catch (final Exception e) {
            DocxRelationshipProcessor.LOGGER.error((Throwable)e, "Error during pokeForOLE_Image", new Object[0]);
            return Optional.empty();
        }
    }
    
    public List<String> getDocImages() {
        return this.docImages;
    }
    
    private static GsonObject createImageGson(final ParsingSpec.DocFormat docFormat, final String px, final String prefix, final String dir, final String docId, final String f) throws IOException {
        final GsonObject imJson = new GsonObject();
        String image = null;
        String file = null;
        switch (docFormat) {
            case CMS: {
                image = f.substring(prefix.length());
                file = String.format("%s/%s-%s-%s", dir, docId, px, image);
                break;
            }
            case OER:
            case QBANK: {
                image = f.substring(prefix.length() + 1);
                file = String.format("%s/%s", dir, f);
                break;
            }
        }
        DocxRelationshipProcessor.LOGGER.debug("Images = %s key = %s", new Object[] { f, file });
        final byte[] bytes = Files.readAllBytes(new File(file).toPath());
        final String content = Base64.getEncoder().encodeToString(bytes);
        imJson.put("name", image);
        imJson.put("base64", content);
        DocxRelationshipProcessor.LOGGER.debug("createImageGson :: Adding Image - %s", new Object[] { image });
        return imJson;
    }
    
    public static GsonObject createImageMap(final ParsingSpec.DocFormat docFormat, final String jobId, final String dir, final String docId) {
        try {
            final GsonObject doc = new GsonObject();
            final GsonArray questions = new GsonArray();
            final GsonArray answers = new GsonArray();
            final File directory = Paths.get(dir, new String[0]).toFile();
            DocxRelationshipProcessor.LOGGER.debug("Looking for images for DocId = %s in %s ", new Object[] { docId, dir });
            final String[] files = directory.list((f, name) -> name.startsWith(docId));
            switch (docFormat) {
                case CMS: {
                    final String qPrefix = String.format("%s-Q-", docId);
                    final String aPrefix = String.format("%s-A-", docId);
                    for (final String f : files) {
                        DocxRelationshipProcessor.LOGGER.debug("Images = %s ", new Object[] { f });
                        if (f.startsWith(qPrefix)) {
                            questions.add(createImageGson(docFormat, "Q", qPrefix, dir, docId, f));
                        }
                        else if (f.startsWith(aPrefix)) {
                            answers.add(createImageGson(docFormat, "A", aPrefix, dir, docId, f));
                        }
                    }
                    break;
                }
                case OER:
                case QBANK: {
                    for (final String f : files) {
                        DocxRelationshipProcessor.LOGGER.debug("Images = %s ", new Object[] { f });
                        final String prefix = docId;
                        questions.add(createImageGson(docFormat, null, prefix, dir, docId, f));
                    }
                    break;
                }
            }
            doc.put("qs", questions);
            doc.put("as", answers);
            return doc;
        }
        catch (final IOException e) {
            DocxRelationshipProcessor.LOGGER.error((Throwable)e, "Error while creating base64 images", new Object[0]);
            throw new RuntimeException(e);
        }
    }
    
    public Map<String, String> getOleToImageEqMap() {
        return this.oleToImageEqMap;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)DocxRelationshipProcessor.class);
        imageKnownFormats = new HashSet<String>(Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/bmp", "image/emf", "image/wmf", "image/x-emf", "image/tiff", "image/eps", "image/gif"));
    }
}
