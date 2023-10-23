package com.facenet.mdm.service.dto;

import java.io.Serializable;

public class ParamDto implements Serializable {

    private String paramValue;
    private String paramDesc;

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
}
