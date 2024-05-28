// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.yojito.minima.gson.GsonDto;

public class PresignedURLResponse extends GsonDto
{
    private final String url;
    private final String uuid;
    
    public PresignedURLResponse(final String url, final String uuid) {
        this.url = url;
        this.uuid = uuid;
    }
    
    public String getUrl() {
        return this.url;
    }
}
