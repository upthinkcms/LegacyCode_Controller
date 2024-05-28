// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

public class TextStyle
{
    private final String color;
    private final String fontFamily;
    private final int fontSize;
    private final boolean isBold;
    private final boolean isItalic;
    private final String underline;
    private final boolean isImage;
    private final String text;
    
    public TextStyle() {
        this.color = null;
        this.fontFamily = null;
        this.fontSize = -1;
        this.isBold = false;
        this.isItalic = false;
        this.underline = null;
        this.isImage = false;
        this.text = null;
    }
    
    public TextStyle(final String text) {
        this.color = null;
        this.fontFamily = null;
        this.fontSize = -1;
        this.isBold = false;
        this.isItalic = false;
        this.underline = null;
        this.isImage = false;
        this.text = text;
    }
    
    public TextStyle(final String color, final String fontFamily, final int fontSize, final boolean isBold, final boolean isItalic, final String underline) {
        this.color = color;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.underline = underline;
        this.isImage = false;
        this.text = null;
    }
    
    public TextStyle(final String color, final String fontFamily, final int fontSize, final boolean isBold, final boolean isItalic, final String underline, final boolean isImage, final String text) {
        this.color = color;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.underline = underline;
        this.isImage = isImage;
        this.text = text;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public String getFontFamily() {
        return this.fontFamily;
    }
    
    public int getFontSize() {
        return this.fontSize;
    }
    
    public boolean getIsBold() {
        return this.isBold;
    }
    
    public boolean getIsItalic() {
        return this.isItalic;
    }
    
    public String getUnderline() {
        return this.underline;
    }
    
    public boolean getIsImage() {
        return this.isImage;
    }
    
    public String getText() {
        return this.text;
    }
}
