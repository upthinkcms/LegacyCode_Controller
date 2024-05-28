// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import org.apache.poi.xwpf.usermodel.XWPFPicture;
import java.util.Iterator;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import java.io.InputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.FileInputStream;
import java.io.File;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;

public class ReadXWPFDocument
{
    private static String TABS(final int tabs) {
        String TABS = "";
        for (int i = 0; i < tabs; ++i) {
            TABS = TABS;
        }
        return TABS;
    }
    
    public static void main(final String[] args) {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/docs/new/Maths";
        final String fileName = "9780134437774-6.4-1CYU.docx";
        final String docId = fileName.substring(0, fileName.length() - 4);
        File file = null;
        final ParsingSpec parsingSpec = ParsingSpecs.FORMAT2;
        XWPFWordExtractor extractor = null;
        try {
            file = new File(directory, fileName);
            final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            final XWPFDocument document = new XWPFDocument((InputStream)fis);
            extractor = new XWPFWordExtractor(document);
            final ParsedDoc parsedDoc = new ParsedDoc(docId, new ArrayList<DocPart>(), null);
            final DocPart root = new DocPart(null, DocPartTypes.START, new ArrayList<DocPart.DocRun>());
            parsedDoc.add(root);
            DocPart current = root;
            int tabs = 0;
            final List<XWPFPictureData> documentAllPictures = document.getAllPictures();
            System.out.printf("documentAllPictures >> %d\n\n\n", documentAllPictures.size());
            if (!documentAllPictures.isEmpty()) {
                documentAllPictures.forEach(xwpfPictureData -> System.out.printf("Name =%s Type=%s Data=%s length=%d\n\n", xwpfPictureData.getFileName(), xwpfPictureData.getPictureType(), xwpfPictureData.getData(), xwpfPictureData.getData().length));
            }
            final List<XWPFParagraph> paragraphs = document.getParagraphs();
            final AtomicInteger atomicInteger = new AtomicInteger();
            for (XWPFParagraph paragraph : paragraphs) {
                final List<XWPFPictureData> pictures = paragraph.getDocument().getAllPictures();
                final List<XWPFRun> runs = paragraph.getRuns();
                final AtomicInteger runIndex = new AtomicInteger();
                for (XWPFRun run : runs) {
                    try {
                        final String text = run.getText(0);
                        final List<XWPFPicture> embeddedPictures = run.getEmbeddedPictures();
                        int picSize = -1;
                        int ePicSize = -1;
                        if (pictures != null) {
                            picSize = pictures.size();
                        }
                        if (embeddedPictures != null) {
                            ePicSize = embeddedPictures.size();
                        }
                        if (text == null) {
                            final String texts = run.text();
                            if (texts != null && !texts.trim().isEmpty()) {
                                System.out.println(run);
                            }
                        }
                    }
                    catch (final Exception e) {
                        System.out.println("Erroro in text pos >> " + e.getMessage());
                    }
                    runIndex.incrementAndGet();
                }
                atomicInteger.incrementAndGet();
                final String paragraphText = paragraph.getText();
                final boolean isMarker = parsingSpec.isMarker(paragraphText);
                if (isMarker) {
                    final boolean isStart = parsingSpec.isMarkerStart(paragraphText);
                    final boolean isEnd = parsingSpec.isMarkerEnd(paragraphText);
                    if (isStart) {
                        final DocPartTypes type = DocPartTypes.getType(paragraphText);
                        System.out.printf("%sSTART %s---------------------------------------------------------------------\n", TABS(tabs), type.types);
                        current = current.child(type);
                        ++tabs;
                    }
                    else if (isEnd) {
                        final DocPartTypes type = DocPartTypes.getType(paragraphText);
                        --tabs;
                        System.out.printf("%sEND %s---------------------------------------------------------------------\n", TABS(tabs), type.types);
                        current.end();
                        current = current.getParent();
                    }
                }
                if (paragraphText != null && !isMarker) {
                    current.addTextRun(paragraphText.trim(), false, false, "", "", "", -1);
                }
                System.out.printf("\n\n", new Object[0]);
            }
        }
        catch (final Exception exep) {
            exep.printStackTrace();
        }
    }
}
