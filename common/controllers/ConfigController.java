// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.controllers;

import com.yojito.minima.api.API;
import com.upthinkexperts.common.domain.DocConfigOptions;
import com.yojito.minima.requests.EmptyRequest;
import com.yojito.minima.netty.HttpCall;
import com.yojito.minima.api.Context;
import com.yojito.minima.logging.MinimaLogger;
import com.yojito.minima.api.ApiController;

public class ConfigController extends ApiController
{
    private static final MinimaLogger LOGGER;
    
    public ConfigController(final Context context) {
        super(context);
    }
    
    @API(path = "/cms/configOptions", corsEnabled = true, authenticated = true, authRole = "AUTHENTICATED")
    public DocConfigOptions getDocConfigOptions(final HttpCall call, final EmptyRequest request) {
        try {
            return (DocConfigOptions)this.context.get("docConfigOptions");
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)ConfigController.class);
    }
}
