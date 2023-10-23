package com.facenet.mdm.service.dto;

import com.querydsl.core.annotations.QueryProjection;

public class InventorySupplierDTO {
    private String vendorId;

    private String vendorName;

    private double price;

    private String unit;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public InventorySupplierDTO(){

    }

    public InventorySupplierDTO(String vendorId, double price, String unit) {
        this.vendorId = vendorId;
        this.price = price;
        this.unit = unit;
    }

    @QueryProjection
    public InventorySupplierDTO(String vendorId, String vendorName, double price, String unit) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.price = price;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "InventorySupplierDTO{" +
            "vendorId=" + vendorId +
            ", vendorName='" + vendorName + '\'' +
            '}';
    }
}
