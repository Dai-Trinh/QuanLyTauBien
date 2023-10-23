package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "param")
public class Param {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "param_id")
    private Integer paramId;

    @Basic
    @Column(name = "param_code")
    private String paramCode;

    @Basic
    @Column(name = "param_value")
    private String paramValue;

    @Basic
    @Column(name = "param_desc")
    private String paramDesc;

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Param that = (Param) o;

        if (paramId != null ? !paramId.equals(that.paramId) : that.paramId != null) return false;
        if (paramCode != null ? !paramCode.equals(that.paramCode) : that.paramCode != null) return false;
        if (paramValue != null ? !paramValue.equals(that.paramValue) : that.paramValue != null) return false;
        if (paramDesc != null ? !paramDesc.equals(that.paramDesc) : that.paramDesc != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramId != null ? paramId.hashCode() : 0;
        result = 31 * result + (paramCode != null ? paramCode.hashCode() : 0);
        result = 31 * result + (paramValue != null ? paramValue.hashCode() : 0);
        result = 31 * result + (paramDesc != null ? paramDesc.hashCode() : 0);
        return result;
    }
}
