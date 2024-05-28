// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class DocResources extends GsonDto
{
    private final String equations3Key;
    private final String images3Key;
    
    public DocResources(final String equations3Key) {
        this.equations3Key = equations3Key;
        this.images3Key = null;
    }
    
    public DocResources(final String equations3Key, final String s3Key) {
        this.equations3Key = equations3Key;
        this.images3Key = s3Key;
    }
    
    public DocResources withEquationS3Key(final String s3Key) {
        return new DocResources(s3Key);
    }
    
    public DocResources withImagesS3Key(final String s3Key) {
        return new DocResources(this.equations3Key, s3Key);
    }
    
    public String getEquations3Key() {
        return this.equations3Key;
    }
    
    public String getImages3Key() {
        return this.images3Key;
    }
}
