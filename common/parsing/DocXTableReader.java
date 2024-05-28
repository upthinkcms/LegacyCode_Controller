// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.Iterator;
import java.util.Collection;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.ArrayList;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

public class DocXTableReader
{
    public static DocPart.DocTable readTable(final XWPFTable table, final XWPFDocument document, final DocxRelationshipProcessor relationshipProcessor) {
        final List<DocPart.DocTableRow> rows = readTableRows(table, document, relationshipProcessor);
        return new DocPart.DocTable(rows);
    }
    
    private static List<DocPart.DocTableRow> readTableRows(final XWPFTable table, final XWPFDocument document, final DocxRelationshipProcessor relationshipProcessor) {
        final List<DocPart.DocTableRow> rows = (List<DocPart.DocTableRow>)table.getRows().stream().map(row -> {
            final List<DocPart.DocTableCell> cells = readTableCells(table, document, row, relationshipProcessor);
            return new DocPart.DocTableRow(cells);
        }).collect(Collectors.toList());
        return rows;
    }
    
    private static List<DocPart.DocTableCell> readTableCells(final XWPFTable table, final XWPFDocument document, final XWPFTableRow row, final DocxRelationshipProcessor relationshipProcessor) {
        final List<DocPart.DocTableCell> cells = (List<DocPart.DocTableCell>)row.getTableCells().stream().map(cell -> {
            final List<DocPart.DocRun> runs = readCell(table, document, row, cell, relationshipProcessor);
            return new DocPart.DocTableCell(runs);
        }).collect(Collectors.toList());
        return cells;
    }
    
    private static List<DocPart.DocRun> readCell(final XWPFTable table, final XWPFDocument document, final XWPFTableRow row, final XWPFTableCell cell, final DocxRelationshipProcessor relationshipProcessor) {
        final List<DocPart.DocRun> cellRuns = new ArrayList<DocPart.DocRun>();
        final int defaultFontSize = document.getStyles().getDefaultRunStyle().getFontSize();
        for (final XWPFParagraph paragraph : cell.getParagraphs()) {
            final List<DocPart.DocRun> list = PoiDocParser.paragraphToDocRuns((IBodyElement)paragraph, relationshipProcessor, defaultFontSize);
            cellRuns.addAll(list);
        }
        return cellRuns;
    }
}
