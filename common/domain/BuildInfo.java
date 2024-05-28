// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import com.yojito.minima.gson.GsonDto;

public class BuildInfo extends GsonDto
{
    private final String branch;
    private final String commitId;
    private final String buildUser;
    private final String buildEmail;
    private final String commitTime;
    
    public BuildInfo(final String branch, final String commitId, final String buildUser, final String buildEmail, final String commitTime) {
        this.branch = branch;
        this.commitId = commitId;
        this.buildUser = buildUser;
        this.buildEmail = buildEmail;
        this.commitTime = commitTime;
    }
}
