// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.io.IOException;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import java.io.InputStream;
import org.apache.poi.hwpf.HWPFDocument;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import com.upthinkexperts.common.parsing.spec.ParsingSpecs;
import java.io.File;

public class ReadHWPFDocument
{
    public static void main(final String[] args) throws IOException {
        final String directory = "/work/myworkspace/druta/projects/upthinkexperts/docs/new/Maths";
        final String fileName = "9780134437774-6.4-1CYU.docx";
        final File file = new File(directory + File.separator + fileName);
        final ParsingSpec parsingSpec = ParsingSpecs.FORMAT2;
        final ParsedDoc parsedDoc = new ParsedDoc("21349-25-25.1P", new ArrayList<DocPart>(), null);
        final DocPart root = new DocPart(null, DocPartTypes.START, new ArrayList<DocPart.DocRun>());
        parsedDoc.add(root);
        final FileInputStream fis = new FileInputStream(file);
        final HWPFDocument document = new HWPFDocument((InputStream)fis);
        final PicturesSource picturesSource = new PicturesSource(document);
        DocPart current = root;
        int tabs = 0;
        final Range range = document.getRange();
        for (int k = 0; k < range.numParagraphs(); ++k) {
            final Paragraph paragraph = range.getParagraph(k);
            final String paragraphText = paragraph.text();
            String TABS = "";
            for (int i = 0; i < tabs; ++i) {
                TABS = TABS;
            }
            final boolean isMarker = parsingSpec.isMarker(paragraphText);
            if (isMarker) {
                final boolean isStart = parsingSpec.isMarkerStart(paragraphText);
                final boolean isEnd = parsingSpec.isMarkerEnd(paragraphText);
                if (isStart) {
                    final DocPartTypes type = DocPartTypes.getType(paragraphText);
                    System.out.printf("%sSTART %s---------------------------------------------------------------------\n", TABS, type.types);
                    current = current.child(type);
                    ++tabs;
                }
                else if (isEnd) {
                    final DocPartTypes type = DocPartTypes.getType(paragraphText);
                    System.out.printf("%sEND %s---------------------------------------------------------------------\n", TABS, type.types);
                    current.end();
                    current = current.getParent();
                    --tabs;
                }
            }
            if (paragraphText != null && !isMarker) {
                current.addTextRun(paragraphText, false, false, "", "", "", -1);
                System.out.printf("%s %d -> %s", TABS, current.paragraphs.size(), paragraphText);
            }
            if (paragraphText.contains("-->")) {}
            for (int j = 0; j < paragraph.numCharacterRuns(); ++j) {
                final CharacterRun cr = paragraph.getCharacterRun(j);
                final Picture picture = picturesSource.getFor(cr);
                if (picture != null) {
                    System.out.printf("%sFound Image -->>ctype=%s height=%d width=%d type=%s\n", TABS, picture.getMimeType(), picture.getHeight(), picture.getWidth(), picture.suggestPictureType());
                }
            }
        }
    }
}
