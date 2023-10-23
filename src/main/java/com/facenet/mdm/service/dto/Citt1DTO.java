package com.facenet.mdm.service.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Citt1DTO implements Serializable {
    private String materialCode;
    private String productCode;
    private String proName;
    private String techName;
    private Integer itemGroupCode;
    private Double productionNorm;
    private String unit;
    private String version;
    private String note;
    private String notice;
    private Boolean isTemplate;
    private Double quantity;
    private Integer status;
    private String kind;
    private final Map<String, String> map = new HashMap<>();

    public Citt1DTO() {
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Integer getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(Integer itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }

    public Double getProductionNorm() {
        return productionNorm;
    }

    public void setProductionNorm(Double productionNorm) {
        this.productionNorm = productionNorm;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, String> getMap() {
        return map;
    }
    public void setMap(String key, String value) {
        map.put(key, value);
    }

}
