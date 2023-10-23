package com.facenet.mdm.service.model;

public class ErrorGroupSearchColumn {

    private Integer errorGroupCodeCulumn;

    private Integer errorGroupNameColumn;

    private Integer errorGroupDescColumn;
    private Integer errorGroupTypeColumn;
    private Integer errorGroupStatusColumn;

    public ErrorGroupSearchColumn() {
    }

    public ErrorGroupSearchColumn(Integer errorGroupCodeCulumn, Integer errorGroupNameColumn, Integer errorGroupDescColumn, Integer errorGroupTypeColumn, Integer errorGroupStatusColumn) {
        this.errorGroupCodeCulumn = errorGroupCodeCulumn;
        this.errorGroupNameColumn = errorGroupNameColumn;
        this.errorGroupDescColumn = errorGroupDescColumn;
        this.errorGroupTypeColumn = errorGroupTypeColumn;
        this.errorGroupStatusColumn = errorGroupStatusColumn;
    }

    public Integer getErrorGroupCodeCulumn() {
        return errorGroupCodeCulumn;
    }

    public void setErrorGroupCodeCulumn(Integer errorGroupCodeCulumn) {
        this.errorGroupCodeCulumn = errorGroupCodeCulumn;
    }

    public Integer getErrorGroupNameColumn() {
        return errorGroupNameColumn;
    }

    public void setErrorGroupNameColumn(Integer errorGroupNameColumn) {
        this.errorGroupNameColumn = errorGroupNameColumn;
    }

    public Integer getErrorGroupDescColumn() {
        return errorGroupDescColumn;
    }

    public void setErrorGroupDescColumn(Integer errorGroupDescColumn) {
        this.errorGroupDescColumn = errorGroupDescColumn;
    }

    public Integer getErrorGroupTypeColumn() {
        return errorGroupTypeColumn;
    }

    public void setErrorGroupTypeColumn(Integer errorGroupTypeColumn) {
        this.errorGroupTypeColumn = errorGroupTypeColumn;
    }

    public Integer getErrorGroupStatusColumn() {
        return errorGroupStatusColumn;
    }

    public void setErrorGroupStatusColumn(Integer errorGroupStatusColumn) {
        this.errorGroupStatusColumn = errorGroupStatusColumn;
    }
}
