// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import com.yojito.minima.gson.GsonDto;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class DocPart
{
    private final DocPart parent;
    private final DocPartTypes type;
    final List<DocRun> paragraphs;
    private boolean cooked;
    private final List<DocPart> children;
    private final Map<String, String> attributes;
    private DocRun currentParagraph;
    
    public DocPart(final DocPart parent, final DocPartTypes type, final List<DocRun> paragraphs) {
        this.cooked = false;
        this.parent = parent;
        this.type = type;
        this.paragraphs = paragraphs;
        this.children = new ArrayList<DocPart>();
        this.attributes = null;
    }
    
    public DocPart(final DocPart parent, final DocPartTypes type, final ArrayList<DocRun> paragraphs, final Map<String, String> attributes) {
        this.cooked = false;
        this.parent = parent;
        this.type = type;
        this.paragraphs = paragraphs;
        this.children = new ArrayList<DocPart>();
        this.attributes = attributes;
    }
    
    private void checkForP() {
        if (this.currentParagraph == null) {
            throw new RuntimeException("No Parent Paragraphs");
        }
    }
    
    private void checkForNotP() {
        if (this.currentParagraph != null) {
            throw new RuntimeException("Un-Matched Paragraphs");
        }
    }
    
    public void startParagraph() {
        this.checkForNotP();
        this.currentParagraph = new DocRun(DocRunType.P, new ArrayList<DocRun>());
    }
    
    public void endParagraph() {
        this.checkForP();
        this.paragraphs.add(this.currentParagraph);
        this.currentParagraph = null;
    }
    
    public void addTextRun(final String text, final boolean isBold, final boolean isItalic, final String underline, final String fontName, final String fontColor, final int fontSize) {
        this.checkForP();
        if (!this.cooked) {
            if (!text.isEmpty()) {
                this.currentParagraph.paragraphs.add(new DocRun(text, isBold, isItalic, underline, fontName, fontColor, fontSize));
            }
            return;
        }
        throw new RuntimeException("DocPart is cooked");
    }
    
    public void addTableRun(final DocRun key) {
        this.checkForP();
        if (!this.cooked) {
            this.currentParagraph.paragraphs.add(key);
            return;
        }
        throw new RuntimeException("DocPart is cooked");
    }
    
    public void addEquationRun(final DocRun key) {
        this.checkForP();
        if (!this.cooked) {
            this.currentParagraph.paragraphs.add(key);
            return;
        }
        throw new RuntimeException("DocPart is cooked");
    }
    
    public void addImageRun(final DocRun key) {
        this.checkForP();
        if (!this.cooked) {
            this.currentParagraph.paragraphs.add(key);
            return;
        }
        throw new RuntimeException("DocPart is cooked");
    }
    
    public void end() {
        if (!this.cooked) {
            this.cooked = true;
            this.endParagraph();
            return;
        }
        throw new RuntimeException("DocPart is already cooked!!");
    }
    
    public DocPart getParent() {
        return this.parent;
    }
    
    public DocPart child(final DocPartTypes type) {
        final DocPart docPart = new DocPart(this, type, new ArrayList<DocRun>());
        docPart.startParagraph();
        this.children.add(docPart);
        return docPart;
    }
    
    public DocPart child(final DocPartTypes type, final Map<String, String> attributes) {
        final DocPart docPart = new DocPart(this, type, new ArrayList<DocRun>(), attributes);
        docPart.startParagraph();
        this.children.add(docPart);
        return docPart;
    }
    
    public DocPartTypes getType() {
        return this.type;
    }
    
    public List<DocRun> getParagraphs() {
        return this.paragraphs;
    }
    
    public List<DocPart> getChildren() {
        return this.children;
    }
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String toString() {
        return "\nDocPart{type=" + this.type + ", paragraphs=" + this.paragraphs + ", cooked=" + this.cooked + ", children=" + this.children + ", attributes=" + this.attributes;
    }
    
    public enum DocRunType
    {
        TEXT, 
        IMAGE, 
        EQUATION, 
        P, 
        TABLE;
    }
    
    public static class DocRun extends GsonDto
    {
        private final DocRunType type;
        private final String text;
        private final List<DocRun> paragraphs;
        private boolean isBold;
        private boolean isItalic;
        private String underline;
        private String fontName;
        private String fontColor;
        private int fontSize;
        private boolean isOMathml;
        private String eqImage;
        private DocTable table;
        
        public DocRun(final DocRunType type, final String text) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = type;
            this.text = text;
            this.paragraphs = null;
        }
        
        public DocRun(final String text, final boolean isBold, final boolean isItalic, final String underline, final String fontName, final String fontColor, final int fontSize) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = DocRunType.TEXT;
            this.text = text;
            this.paragraphs = null;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.underline = underline;
            this.fontName = fontName;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
        }
        
        public DocRun(final DocRunType equation, final String mathml, final boolean isBold, final boolean isItalic, final String underline, final String fontName, final String fontColor, final int fontSize, final boolean isOMathml) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = equation;
            this.text = mathml;
            this.paragraphs = null;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.underline = underline;
            this.fontName = fontName;
            this.fontColor = fontColor;
            this.fontSize = fontSize;
            this.isOMathml = isOMathml;
        }
        
        public DocRun(final DocRunType type, final List<DocRun> paragraphs) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = type;
            this.paragraphs = paragraphs;
            this.text = null;
        }
        
        public DocRun(final DocRunType equation, final String mathml, final boolean isOMathml) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = equation;
            this.text = mathml;
            this.paragraphs = null;
            this.isOMathml = isOMathml;
        }
        
        public DocRun(final DocRunType equation, final String mathml, final String eqImage) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = equation;
            this.text = mathml;
            this.paragraphs = null;
            this.eqImage = eqImage;
        }
        
        public DocRun(final DocRunType table, final DocTable docTable) {
            this.isBold = false;
            this.isItalic = false;
            this.underline = null;
            this.fontName = null;
            this.fontColor = null;
            this.fontSize = -1;
            this.isOMathml = false;
            this.eqImage = null;
            this.table = null;
            this.type = DocRunType.TABLE;
            this.text = null;
            this.paragraphs = null;
            this.eqImage = null;
            this.table = docTable;
        }
        
        public DocRunType getType() {
            return this.type;
        }
        
        public String getText() {
            return this.text;
        }
        
        public List<DocRun> getParagraphs() {
            return this.paragraphs;
        }
        
        public boolean isBold() {
            return this.isBold;
        }
        
        public boolean isItalic() {
            return this.isItalic;
        }
        
        public String getUnderline() {
            return this.underline;
        }
        
        public String getFontName() {
            return this.fontName;
        }
        
        public String getFontColor() {
            return this.fontColor;
        }
        
        public int getFontSize() {
            return this.fontSize;
        }
        
        public DocTable getTable() {
            return this.table;
        }
    }
    
    public static class DocTableCell extends GsonDto
    {
        private final List<DocRun> runs;
        
        public DocTableCell(final List<DocRun> runs) {
            this.runs = runs;
        }
        
        public List<DocRun> getRuns() {
            return this.runs;
        }
    }
    
    public static class DocTableRow extends GsonDto
    {
        private final List<DocTableCell> cells;
        
        public DocTableRow(final List<DocTableCell> cells) {
            this.cells = cells;
        }
        
        public List<DocTableCell> getCells() {
            return this.cells;
        }
    }
    
    public static class DocTable extends GsonDto
    {
        private final List<DocTableRow> rows;
        
        public DocTable(final List<DocTableRow> rows) {
            this.rows = rows;
        }
        
        public List<DocTableRow> getRows() {
            return this.rows;
        }
    }
}
