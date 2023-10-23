package com.facenet.mdm.service.model;

import com.querydsl.core.types.Order;

public class ErrorInput {

    private int page;
    private int pageSize;

    private ErrorFilter filter;

    private ErrorSearchColumn operator;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ErrorFilter getFilter() {
        return filter;
    }

    public void setFilter(ErrorFilter filter) {
        this.filter = filter;
    }

    public ErrorSearchColumn getOperator() {
        return operator;
    }

    public void setOperator(ErrorSearchColumn operator) {
        this.operator = operator;
    }
}
