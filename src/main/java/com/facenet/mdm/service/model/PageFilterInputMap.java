package com.facenet.mdm.service.model;

import com.facenet.mdm.service.dto.response.PageResponse;
import java.util.Map;
import javax.validation.constraints.NotNull;

public class PageFilterInputMap {

    @NotNull(message = "pageNumber must not be null")
    private Integer pageNumber;

    @NotNull(message = "pageSize must not be null")
    private Integer pageSize;

    @NotNull(message = "filter must not be null")
    private Map<String, String> filter;

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

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
}
