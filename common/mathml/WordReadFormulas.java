// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.mathml;

import java.util.Iterator;
import java.util.List;
import java.awt.Desktop;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import java.util.ArrayList;
import java.io.InputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import javax.xml.transform.TransformerFactory;

public class WordReadFormulas
{
    static TransformerFactory tFactory;
    
    public static String getMathML(final CTOMath ctomath) throws Exception {
        final StreamSource stylesource = new StreamSource(WordReadFormulas.class.getResourceAsStream("/omml2mml.xsl"));
        final Transformer transformer = WordReadFormulas.tFactory.newTransformer(stylesource);
        final Node node = ctomath.getDomNode();
        final DOMSource source = new DOMSource(node);
        final StringWriter stringwriter = new StringWriter();
        final StreamResult result = new StreamResult(stringwriter);
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(source, result);
        String mathML = stringwriter.toString();
        stringwriter.close();
        mathML = mathML.replaceAll("xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"", "");
        mathML = mathML.replaceAll("xmlns:mml", "xmlns");
        mathML = mathML.replaceAll("mml:", "");
        return mathML;
    }
    
    public static void main(final String[] args) throws Exception {
        final String dir = "/work/myworkspace/druta/projects/upthinkexperts/docs/prepared";
        final String filePath = dir + File.separator + "21349-25-25.26AP.docx";
        final XWPFDocument document = new XWPFDocument((InputStream)new FileInputStream(filePath));
        final List<String> mathMLList = new ArrayList<String>();
        for (final IBodyElement ibodyelement : document.getBodyElements()) {
            if (ibodyelement.getElementType().equals((Object)BodyElementType.PARAGRAPH)) {
                final XWPFParagraph paragraph = (XWPFParagraph)ibodyelement;
                for (final CTOMath ctomath : paragraph.getCTP().getOMathList()) {
                    mathMLList.add(getMathML(ctomath));
                }
                for (final CTOMathPara ctomathpara : paragraph.getCTP().getOMathParaList()) {
                    for (final CTOMath ctomath2 : ctomathpara.getOMathList()) {
                        mathMLList.add(getMathML(ctomath2));
                    }
                }
            }
            else {
                if (!ibodyelement.getElementType().equals((Object)BodyElementType.TABLE)) {
                    continue;
                }
                final XWPFTable table = (XWPFTable)ibodyelement;
                for (final XWPFTableRow row : table.getRows()) {
                    for (final XWPFTableCell cell : row.getTableCells()) {
                        for (final XWPFParagraph paragraph2 : cell.getParagraphs()) {
                            for (final CTOMath ctomath3 : paragraph2.getCTP().getOMathList()) {
                                mathMLList.add(getMathML(ctomath3));
                            }
                            for (final CTOMathPara ctomathpara2 : paragraph2.getCTP().getOMathParaList()) {
                                for (final CTOMath ctomath4 : ctomathpara2.getOMathList()) {
                                    mathMLList.add(getMathML(ctomath4));
                                }
                            }
                        }
                    }
                }
            }
        }
        document.close();
        final String encoding = "UTF-8";
        final FileOutputStream fos = new FileOutputStream("result.html");
        final OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);
        writer.write("<!DOCTYPE html>\n");
        writer.write("<html lang=\"en\">");
        writer.write("<head>");
        writer.write("<meta charset=\"utf-8\"/>");
        writer.write("<script type=\"text/javascript\"");
        writer.write(" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=MML_CHTML\"");
        writer.write(">");
        writer.write("</script>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<p>Following formulas was found in Word document: </p>");
        int i = 1;
        for (String mathML : mathMLList) {
            writer.write("<p>Formula" + i++ + ":</p>");
            writer.write(mathML);
            writer.write("<p/>");
        }
        writer.write("</body>");
        writer.write("</html>");
        writer.close();
        Desktop.getDesktop().browse(new File("result.html").toURI());
    }
    
    static {
        WordReadFormulas.tFactory = TransformerFactory.newInstance();
    }
}
