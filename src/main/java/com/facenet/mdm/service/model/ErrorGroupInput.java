package com.facenet.mdm.service.model;

import com.querydsl.core.types.Order;

public class ErrorGroupInput {

    private int page;
    private int pageSize;

    private ErrorGroupFilter filter;

    private ErrorGroupSearchColumn operator;

    private String sortProperty;

    private Order sortOrder;

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

    public ErrorGroupFilter getFilter() {
        return filter;
    }

    public void setFilter(ErrorGroupFilter filter) {
        this.filter = filter;
    }

    public ErrorGroupSearchColumn getOperator() {
        return operator;
    }

    public void setOperator(ErrorGroupSearchColumn operator) {
        this.operator = operator;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public Order getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Order sortOrder) {
        this.sortOrder = sortOrder;
    }
}
