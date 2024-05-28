// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;

public interface DocParser
{
    ParsedDoc parse(final String p0, final boolean p1, final ParsingSpec p2, final File p3, final String p4, final String p5) throws IOException;
    
    ParsedDoc parse(final String p0, final boolean p1, final ParsingSpec p2, final InputStream p3, final String p4, final String p5) throws IOException;
}
