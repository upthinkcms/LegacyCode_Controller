// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

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
