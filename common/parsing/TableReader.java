// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import java.util.HashMap;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.FileInputStream;
import java.io.File;

public class TableReader
{
    public static void main(final String[] args) {
        try {
            final String directory = "/work/myworkspace/druta/projects/upthinkexperts/docs/Chemistry-20200723T074345Z-001/Chemistry";
            final String fileName = "DocWithTable.docx";
            final FileInputStream fis = new FileInputStream(new File(directory, fileName));
            final XWPFDocument xdoc = new XWPFDocument(OPCPackage.open((InputStream)fis));
            final Iterator<IBodyElement> bodyElementIterator = xdoc.getBodyElementsIterator();
            final Map<Integer, IBodyElement> elementOrderMap = new HashMap<Integer, IBodyElement>();
            int index = 0;
            while (bodyElementIterator.hasNext()) {
                final IBodyElement element = bodyElementIterator.next();
                elementOrderMap.put(index++, element);
                System.out.println("Type -->" + element.getElementType().name());
                System.out.printf("\t getPartType =%s EType=%s getBodyType =%s\n", element.getPartType(), element.getElementType(), element.getBody().getPartType());
                System.out.printf("\t Relations =%s\n", element.getPart().getRelations());
                if ("TABLE".equalsIgnoreCase(element.getElementType().name())) {
                    final List<XWPFTable> tableList = element.getBody().getTables();
                    for (XWPFTable table : tableList) {
                        System.out.println("Total Number of Rows of Table:" + table.getNumberOfRows());
                        for (int i = 0; i < table.getRows().size(); ++i) {
                            for (int j = 0; j < table.getRow(i).getTableCells().size(); ++j) {
                                System.out.println(table.getRow(i).getCell(j).getText());
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
