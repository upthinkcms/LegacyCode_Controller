// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.QBankTopic;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class FileUploadTopicsResponse extends GsonDto
{
    private final List<QBankTopic> topics;
    
    public FileUploadTopicsResponse(final List<QBankTopic> topics) {
        this.topics = topics;
    }
    
    public List<QBankTopic> getTopics() {
        return this.topics;
    }
}
