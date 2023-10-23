package com.facenet.mdm.service.dto;

public class ErrorDetailDTO {

    private String errorCode;
    private String errorGroupCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorGroupCode() {
        return errorGroupCode;
    }

    public void setErrorGroupCode(String errorGroupCode) {
        this.errorGroupCode = errorGroupCode;
    }
}
