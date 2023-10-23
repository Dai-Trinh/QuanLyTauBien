package com.facenet.mdm.service.model;

public class ErrorSearchColumn {

    private Integer errorCodeCulumn;

    private Integer errorNameColumn;

    private Integer errorDescColumn;
    private Integer errorTypeColumn;
    private Integer errorStatusColumn;

    public ErrorSearchColumn() {
    }

    public ErrorSearchColumn(Integer errorCodeCulumn, Integer errorNameColumn, Integer errorDescColumn, Integer errorTypeColumn, Integer errorStatusColumn) {
        this.errorCodeCulumn = errorCodeCulumn;
        this.errorNameColumn = errorNameColumn;
        this.errorDescColumn = errorDescColumn;
        this.errorTypeColumn = errorTypeColumn;
        this.errorStatusColumn = errorStatusColumn;
    }

    public Integer getErrorCodeCulumn() {
        return errorCodeCulumn;
    }

    public void setErrorCodeCulumn(Integer errorCodeCulumn) {
        this.errorCodeCulumn = errorCodeCulumn;
    }

    public Integer getErrorNameColumn() {
        return errorNameColumn;
    }

    public void setErrorNameColumn(Integer errorNameColumn) {
        this.errorNameColumn = errorNameColumn;
    }

    public Integer getErrorDescColumn() {
        return errorDescColumn;
    }

    public void setErrorDescColumn(Integer errorDescColumn) {
        this.errorDescColumn = errorDescColumn;
    }

    public Integer getErrorTypeColumn() {
        return errorTypeColumn;
    }

    public void setErrorTypeColumn(Integer errorTypeColumn) {
        this.errorTypeColumn = errorTypeColumn;
    }

    public Integer getErrorStatusColumn() {
        return errorStatusColumn;
    }

    public void setErrorStatusColumn(Integer errorStatusColumn) {
        this.errorStatusColumn = errorStatusColumn;
    }
}
