package com.facenet.mdm.service.model;

public class EmployeeInput {

    private int page;

    private int pageSize;

    private EmployeeFilter filter;

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

    public EmployeeFilter getFilter() {
        return filter;
    }

    public void setFilter(EmployeeFilter filter) {
        this.filter = filter;
    }
}
