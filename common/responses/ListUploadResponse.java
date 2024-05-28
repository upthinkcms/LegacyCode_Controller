// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.responses;

import com.upthinkexperts.common.domain.UploadJob;
import java.util.List;
import com.yojito.minima.gson.GsonDto;

public class ListUploadResponse extends GsonDto
{
    private final List<UploadJob> list;
    private final int total;
    
    public ListUploadResponse(final List<UploadJob> list, final int total) {
        this.list = list;
        this.total = total;
    }
    
    public ListUploadResponse(final List<UploadJob> list) {
        this.list = list;
        this.total = 0;
    }
    
    public List<UploadJob> getList() {
        return this.list;
    }
    
    public int getTotal() {
        return this.total;
    }
}
