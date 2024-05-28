// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

public class ListRequest
{
    private final int page;
    private final int limit;
    private final String sortField;
    private final String sortDirection;
    
    public ListRequest(final int page, int limit, final String sortField, final String sortDirection) {
        if (limit <= 0) {
            limit = 10;
        }
        this.limit = limit;
        if (page > 0) {
            this.page = (page - 1) * limit;
        }
        else {
            this.page = 0;
        }
        if (sortField != null && !sortField.isEmpty()) {
            this.sortField = sortField;
        }
        else {
            this.sortField = "created_at";
        }
        if (sortDirection != null && !sortDirection.isEmpty()) {
            this.sortDirection = sortDirection;
        }
        else {
            this.sortDirection = "DESC";
        }
    }
    
    public int getPage() {
        return this.page;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public String getSortField() {
        return this.sortField;
    }
    
    public String getSortDirection() {
        return this.sortDirection;
    }
}
