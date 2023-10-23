package com.facenet.mdm.service.model;

import com.facenet.mdm.service.dto.ErrorDTO;

import java.util.List;

public class ErrorResponse {

    private ResultCode result;

    private int totalData;
    private List<ErrorDTO> data;

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

    public List<ErrorDTO> getData() {
        return data;
    }

    public void setData(List<ErrorDTO> data) {
        this.data = data;
    }
}
