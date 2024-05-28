// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.UploadJob;
import com.upthinkexperts.common.domain.JobFile;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class UploadJobDetails extends GsonDto
{
    private final List<JobFile> files;
    private final UploadJob uploadJob;
    
    public UploadJobDetails(final List<JobFile> files, final UploadJob uploadJob) {
        this.files = files;
        this.uploadJob = uploadJob;
    }
}
