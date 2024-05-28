// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

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
