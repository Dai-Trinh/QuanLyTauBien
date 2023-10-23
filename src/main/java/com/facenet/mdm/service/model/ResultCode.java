package com.facenet.mdm.service.model;

public class ResultCode {

    private String responseCode;
    private String message;
    private boolean ok;

    public ResultCode(String responseCode, String message, boolean ok) {
        this.responseCode = responseCode;
        this.message = message;
        this.ok = ok;
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
