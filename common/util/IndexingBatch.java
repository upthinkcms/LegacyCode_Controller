// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.util.ArrayList;
import java.util.List;

public class IndexingBatch
{
    private final List<IndexCommand> list;
    private boolean reIndexing;
    
    public IndexingBatch() {
        this.list = new ArrayList<IndexCommand>();
        this.reIndexing = false;
    }
    
    public synchronized void add(final IndexCommand command) {
        this.list.add(command);
    }
    
    public List<IndexCommand> getList() {
        return this.list;
    }
    
    public void setReIndexing(final boolean reIndexing) {
        this.reIndexing = reIndexing;
    }
    
    public boolean isReIndexing() {
        return this.reIndexing;
    }
}
