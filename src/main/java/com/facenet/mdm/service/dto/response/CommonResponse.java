package com.facenet.mdm.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    protected Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected T data;

    public CommonResponse() {
    }

    public CommonResponse success(){
        this.result = new Result().errorCode("00").message("Thành công").isOk(true);
        return this;
    }

    public CommonResponse success(String message){
        this.result = new Result().errorCode("00").message(message).isOk(true);
        return this;
    }

    public CommonResponse result(String errorCode,String errorMessage, boolean ok){
        this.result = new Result().errorCode(errorCode).message(errorMessage).isOk(ok);
        return this;
    }

    public CommonResponse errorCode(String errorCode){
        if(this.result == null){
            this.result = new Result();
        }
        this.result.errorCode(errorCode);
        return this;
    }

    public CommonResponse message(String message){
        if(this.result == null){
            this.result = new Result();
        }
        this.result.message(message);
        return this;
    }

    public CommonResponse isOk(boolean ok){
        if(this.result == null){
            this.result = new Result();
        }
        this.result.isOk(ok);
        return this;
    }
    public CommonResponse data(T data){
        this.data = data;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
