// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

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
