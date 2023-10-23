package com.facenet.mdm.service.dto;

import com.querydsl.core.annotations.QueryProjection;

public class ItemInVendorDTO {
    private String vendorCode;

    private String itemCode;

    private String itemName;

    private String techName;

    private Integer groupType;

    private String groupName;

    private long onHand;

    private String unit;

    private String type;

    private String status;

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getOnHand() {
        return onHand;
    }

    public void setOnHand(long onHand) {
        this.onHand = onHand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ItemInVendorDTO(){

    }

    @QueryProjection
    public ItemInVendorDTO(String vendorCode, String itemCode, String itemName, String techName, Integer groupType, String groupName, long onHand, String unit, String type, String status) {
        this.vendorCode = vendorCode;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.techName = techName;
        this.groupType = groupType;
        this.groupName = groupName;
        this.onHand = onHand;
        this.unit = unit;
        this.type = type;
        this.status = status;
    }

    @Override
    public String toString() {
        return "DetailBomVersionDTO{" +
            "itemCode=" + itemCode +
            ", itemName='" + itemName + '\'' +
            ", techName='" + techName + '\'' +
            ", groupType=" + groupType +
            ", groupName='" + groupName + '\'' +
            ", onHand='" + onHand + '\'' +
            ", unit=" + unit +
            ", type='" + type + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}
