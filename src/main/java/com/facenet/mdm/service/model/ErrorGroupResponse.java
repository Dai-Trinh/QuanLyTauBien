package com.facenet.mdm.service.model;

import com.facenet.mdm.service.dto.ErrorGroupDTO;

import java.util.List;

public class ErrorGroupResponse {

    private ResultCode result;

    private int totalData;

    private List<ErrorGroupDTO> data;

    public ResultCode getResult() {
        return result;
    }

    public void setResult(ResultCode result) {
        this.result = result;
    }

    public int getTotalData() {
        return totalData;
    }

    public void setTotalData(int totalData) {
        this.totalData = totalData;
    }

    public List<ErrorGroupDTO> getData() {
        return data;
    }

    public void setData(List<ErrorGroupDTO> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ErrorGroupResponse{" +
            "result=" + result +
            ", totalData=" + totalData +
            ", data=" + data +
            '}';
    }
}
