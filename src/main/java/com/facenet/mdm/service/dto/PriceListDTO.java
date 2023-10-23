package com.facenet.mdm.service.dto;

import java.io.Serializable;

public class PriceListDTO implements Serializable {

    private Integer minQuantity;
    private Integer maxQuantity;
    private Double unitPrice;
    private String currency;

    public PriceListDTO() {
    }

    public PriceListDTO(Integer minQuantity, Integer maxQuantity, Double unitPrice, String currency) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.unitPrice = unitPrice;
        this.currency = currency;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
