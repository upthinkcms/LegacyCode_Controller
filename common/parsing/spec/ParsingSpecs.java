// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.spec;

public class ParsingSpecs
{
    public static final ParsingSpec FORMAT2;
    public static final ParsingSpec FORMAT3;
    public static final ParsingSpec FORMAT1;
    public static final ParsingSpec QBANK;
    public static final ParsingSpec PLAIN;
    public static final ParsingSpec OPENSTAX;
    
    public static ParsingSpec getParsingSpec(final String parsingSpec) {
        switch (parsingSpec) {
            case "FORMAT2": {
                return ParsingSpecs.FORMAT2;
            }
            case "FORMAT3": {
                return ParsingSpecs.FORMAT3;
            }
            case "FORMAT1": {
                return ParsingSpecs.FORMAT1;
            }
            case "QBANK": {
                return ParsingSpecs.QBANK;
            }
            case "PLAIN": {
                return ParsingSpecs.PLAIN;
            }
            case "OPENSTAX": {
                return ParsingSpecs.OPENSTAX;
            }
            default: {
                throw new RuntimeException("Unknown Parsing SpecName " + parsingSpec);
            }
        }
    }
    
    static {
        FORMAT2 = new OldDocParsingSpec();
        FORMAT3 = new NewSpec_Drop2();
        FORMAT1 = new Format1();
        QBANK = new QBankSpec();
        PLAIN = new PlainDocSpec();
        OPENSTAX = new OpenstaxSpec();
    }
}
