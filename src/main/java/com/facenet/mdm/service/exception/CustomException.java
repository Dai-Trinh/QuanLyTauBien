package com.facenet.mdm.service.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{

    private String errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    private String[] args;
    public CustomException(){
        super();
    }

    public CustomException(String errorCode){
        this.httpStatus = HttpStatus.OK;
        this.errorCode = errorCode;
    }

    public CustomException(String errorCode,String ... args){
        this.httpStatus = HttpStatus.OK;
        this.errorCode = errorCode;
        this.args = args;
    }

    public CustomException(HttpStatus httpStatus, String errorCode,String ... args){
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = args;
    }

    public CustomException(HttpStatus httpStatus,String errorCode){
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
