// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.DBSubject;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class FileUploadSubjectsResponse extends GsonDto
{
    private final List<DBSubject> subjects;
    
    public FileUploadSubjectsResponse(final List<DBSubject> subjects) {
        this.subjects = subjects;
    }
    
    public List<DBSubject> getSubjects() {
        return this.subjects;
    }
}
