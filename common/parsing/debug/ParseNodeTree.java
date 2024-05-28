// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing.debug;

import java.util.ArrayList;
import java.util.List;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.gson.GsonDto;

public class ParseNodeTree extends GsonDto
{
    private static final MinimaLogger LOGGER;
    private final String node;
    private final transient ParseNodeTree parent;
    private final List<ParseNodeTree> nodes;
    
    public ParseNodeTree(final String node, final ParseNodeTree parent) {
        this.nodes = new ArrayList<ParseNodeTree>();
        this.node = node;
        this.parent = parent;
    }
    
    public ParseNodeTree createChild(final String name) {
        final ParseNodeTree child = new ParseNodeTree(name, this);
        this.nodes.add(child);
        ParseNodeTree.LOGGER.debug("Adding new node %s to %s", new Object[] { name, this.node });
        return child;
    }
    
    public ParseNodeTree getParent() {
        ParseNodeTree.LOGGER.debug("Returning this node = %s, parent = %s", new Object[] { this.node, this.parent.node });
        return this.parent;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)ParseNodeTree.class);
    }
}
