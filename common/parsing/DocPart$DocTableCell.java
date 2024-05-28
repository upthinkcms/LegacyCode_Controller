// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

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
