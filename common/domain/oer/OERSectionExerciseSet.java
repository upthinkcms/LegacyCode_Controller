// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.domain.oer;

import java.util.Map;
import com.upthinkexperts.common.domain.DocSection;
import java.util.List;

public class OERSectionExerciseSet extends OERNode
{
    private final List<OERSectionExercise> exerciseList;
    
    public OERSectionExerciseSet(final DocSection section, final Map<String, String> attributes, final List<OERSectionExercise> exerciseList) {
        super(section, attributes);
        this.exerciseList = exerciseList;
    }
}
