// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.yojito.minima.gson.GsonObject;
import com.yojito.minima.gson.GsonDto;

public class GetEquationsResponse extends GsonDto
{
    private final GsonObject equationJson;
    
    public GetEquationsResponse(final GsonObject equationJson) {
        this.equationJson = equationJson;
    }
    
    public GsonObject getEquationJson() {
        return this.equationJson;
    }
}
