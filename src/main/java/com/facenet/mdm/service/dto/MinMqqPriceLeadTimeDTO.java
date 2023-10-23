package com.facenet.mdm.service.dto;

import java.util.Date;


public class MinMqqPriceLeadTimeDTO {

    private Integer leadTime;
    private Double mqqPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Date dueDate;
    private String currency;

    public MinMqqPriceLeadTimeDTO() {
    }

    public MinMqqPriceLeadTimeDTO(Integer leadTime, Double mqqPrice, Integer minQuantity, Integer maxQuantity, Date dueDate, String currency) {
        this.leadTime = leadTime;
        this.mqqPrice = mqqPrice;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.dueDate = dueDate;
        this.currency = currency;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Double getMqqPrice() {
        return mqqPrice;
    }

    public void setMqqPrice(Double mqqPrice) {
        this.mqqPrice = mqqPrice;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
