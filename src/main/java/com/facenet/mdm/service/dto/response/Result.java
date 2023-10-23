package com.facenet.mdm.service.dto.response;

public class Result {
    private String responseCode;
    private String message;
    private boolean ok;

    public Result() {

    }

    public Result errorCode(String errorCode) {
        this.responseCode = errorCode;
        return this;
    }

    public Result message(String errorMessage) {
        this.message = errorMessage;
        return this;
    }

    public Result isOk(boolean ok){
        this.ok = ok;
        return this;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
