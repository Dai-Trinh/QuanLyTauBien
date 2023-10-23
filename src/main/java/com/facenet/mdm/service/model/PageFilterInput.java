package com.facenet.mdm.service.model;

import com.querydsl.core.types.Order;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public class PageFilterInput<T> {

    @NotNull(message = "pageNumber must not be null")
    private Integer pageNumber;

    @NotNull(message = "pageSize must not be null")
    private Integer pageSize;

    @NotNull(message = "filter must not be null")
    private T filter;

    private String common;

    private String sortProperty;

    private Order sortOrder;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
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
