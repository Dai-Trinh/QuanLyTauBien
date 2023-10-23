package com.facenet.mdm.service.dto;

import java.util.List;

public class DataToFillBom {

    private String productCode;

    private String productName;
    private String techName;
    private String version;
    private Integer itemGroup;
    private Double productionNorm;
    private String unit;
    private String vendor;
    private String wareHouse;
    private String materialReplaceCode;
    private String materialReplaceName;

    public DataToFillBom() {}

    public DataToFillBom(
        String productCode,
        String productName,
        String techName,
        String version,
        Integer itemGroup,
        Double productionNorm,
        String unit,
        String wareHouse,
        String materialReplaceCode
    ) {
        this.productCode = productCode;
        this.productName = productName;
        this.techName = techName;
        this.version = version;
        this.itemGroup = itemGroup;
        this.productionNorm = productionNorm;
        this.unit = unit;
        this.wareHouse = wareHouse;
        this.materialReplaceCode = materialReplaceCode;
    }

    public DataToFillBom(
        String productCode,
        String productName,
        String techName,
        String version,
        Integer itemGroup,
        Double productionNorm,
        String unit,
        String wareHouse,
        String materialReplaceCode,
        String vendor
    ) {
        this.productCode = productCode;
        this.productName = productName;
        this.techName = techName;
        this.version = version;
        this.itemGroup = itemGroup;
        this.productionNorm = productionNorm;
        this.unit = unit;
        this.wareHouse = wareHouse;
        this.materialReplaceCode = materialReplaceCode;
        this.vendor = vendor;
    }

    public DataToFillBom(
        String productCode,
        String productName,
        String techName,
        String version,
        Integer itemGroup,
        String unit,
        String wareHouse
    ) {
        this.productCode = productCode;
        this.productName = productName;
        this.techName = techName;
        this.version = version;
        this.itemGroup = itemGroup;
        this.unit = unit;
        this.wareHouse = wareHouse;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(Integer itemGroup) {
        this.itemGroup = itemGroup;
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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }

    public String getMaterialReplaceCode() {
        return materialReplaceCode;
    }

    public void setMaterialReplaceCode(String materialReplaceCode) {
        this.materialReplaceCode = materialReplaceCode;
    }

    public String getMaterialReplaceName() {
        return materialReplaceName;
    }

    public void setMaterialReplaceName(String materialReplaceName) {
        this.materialReplaceName = materialReplaceName;
    }
}
