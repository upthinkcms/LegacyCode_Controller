// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain;

import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class QBankSet extends GsonDto
{
    private final List<QBankSetPart> parts;
    private final double maxScore;
    private final boolean isSummative;
    private final int duration;
    private final int attempts;
    
    public QBankSet(final List<QBankSetPart> parts, final double maxScore, final boolean isSummative, final int duration) {
        this.parts = parts;
        this.maxScore = maxScore;
        this.isSummative = isSummative;
        this.duration = duration;
        this.attempts = -1;
    }
    
    public QBankSet(final List<QBankSetPart> parts, final double maxScore, final boolean isSummative, final int duration, final int attempts) {
        this.parts = parts;
        this.maxScore = maxScore;
        this.isSummative = isSummative;
        this.duration = duration;
        this.attempts = attempts;
    }
    
    public List<QBankSetPart> getParts() {
        return this.parts;
    }
    
    public double getMaxScore() {
        return this.maxScore;
    }
    
    public boolean isSummative() {
        return this.isSummative;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public QBankSet withParts(final List<QBankSetPart> setPartWithNames) {
        return new QBankSet(setPartWithNames, this.maxScore, this.isSummative, this.duration);
    }
    
    public int getAttempts() {
        return this.attempts;
    }
}
