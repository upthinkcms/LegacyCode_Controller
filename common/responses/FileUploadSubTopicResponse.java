// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.QBankSubTopic;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class FileUploadSubTopicResponse extends GsonDto
{
    private final List<QBankSubTopic> subTopicList;
    
    public FileUploadSubTopicResponse(final List<QBankSubTopic> subTopicList) {
        this.subTopicList = subTopicList;
    }
    
    public List<QBankSubTopic> getSubTopicList() {
        return this.subTopicList;
    }
}
