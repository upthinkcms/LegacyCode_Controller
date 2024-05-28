// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.util.concurrent.Executor;
import org.jdbi.v3.core.Jdbi;

public class CmsCommonConfig
{
    private final Jdbi db;
    private final String bucket;
    private final String region;
    private final String sOfficePath;
    private final String mogrifyPath;
    private final Executor workerPool;
    private final TextIndex textIndex;
    private final Integer calabashMinMemory;
    private final String calabashPath;
    private final String jdkPath;
    
    public CmsCommonConfig(final Jdbi db, final String bucket, final String region, final String sOfficePath, final String mogrifyPath, final Executor workerPool, final TextIndex textIndex, final Integer calabashMinMemory, final String calabashPath, final String jdkPath) {
        this.db = db;
        this.bucket = bucket;
        this.region = region;
        this.sOfficePath = sOfficePath;
        this.mogrifyPath = mogrifyPath;
        this.workerPool = workerPool;
        this.textIndex = textIndex;
        this.calabashMinMemory = calabashMinMemory;
        this.calabashPath = calabashPath;
        this.jdkPath = jdkPath;
    }
    
    public Jdbi getDb() {
        return this.db;
    }
    
    public String getBucket() {
        return this.bucket;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public String getsOfficePath() {
        return this.sOfficePath;
    }
    
    public String getMogrifyPath() {
        return this.mogrifyPath;
    }
    
    public Executor getWorkerPool() {
        return this.workerPool;
    }
    
    public TextIndex getTextIndex() {
        return this.textIndex;
    }
    
    public Integer getCalabashMinMemory() {
        return this.calabashMinMemory;
    }
    
    public String getCalabashPath() {
        return this.calabashPath;
    }
    
    public String getJdkPath() {
        return this.jdkPath;
    }
    
    @Override
    public String toString() {
        return "CmsCommonConfig{db=" + this.db + ", bucket='" + this.bucket + "', region='" + this.region + "', sOfficePath='" + this.sOfficePath + "', mogrifyPath='" + this.mogrifyPath + "', workerPool=" + this.workerPool + ", textIndex=" + this.textIndex + ", calabashMinMemory=" + this.calabashMinMemory + ", calabashPath='" + this.calabashPath + "', jdkPath='" + this.jdkPath + "'}";
    }
    
    public CmsCommonConfig withTextIndex(final TextIndex textIndex) {
        return new CmsCommonConfig(this.db, this.bucket, this.region, this.sOfficePath, this.mogrifyPath, this.workerPool, textIndex, this.calabashMinMemory, this.calabashPath, this.jdkPath);
    }
}
