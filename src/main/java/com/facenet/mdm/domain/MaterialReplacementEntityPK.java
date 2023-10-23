package com.facenet.mdm.domain;

import java.io.Serializable;

public class MaterialReplacementEntityPK implements Serializable {
    private String materialCode;
    private String materialReplacementCode;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialReplacementCode() {
        return materialReplacementCode;
    }

    public void setMaterialReplacementCode(String materialReplacementCode) {
        this.materialReplacementCode = materialReplacementCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaterialReplacementEntityPK that = (MaterialReplacementEntityPK) o;

        if (materialCode != null ? !materialCode.equals(that.materialCode) : that.materialCode != null) return false;
        if (materialReplacementCode != null ? !materialReplacementCode.equals(that.materialReplacementCode) : that.materialReplacementCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = materialCode != null ? materialCode.hashCode() : 0;
        result = 31 * result + (materialReplacementCode != null ? materialReplacementCode.hashCode() : 0);
        return result;
    }
}
