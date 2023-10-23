package com.facenet.mdm.service.dto;

import java.util.Date;

public class MoqDTO {
    private Integer itemPriceId;
    private String vendorCode;
    private String itemCode;
    private Integer rangeStart;
    private Integer rangeEnd;
    private Double price;
    private Integer leadTime;
    private String currency;
    private Date dueDate;
    private String note;

    public Integer getItemPriceId() {
        return itemPriceId;
    }

    public void setItemPriceId(Integer itemPriceId) {
        this.itemPriceId = itemPriceId;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public MoqDTO() {
    }

    public MoqDTO(Integer itemPriceId, String vendorCode, String itemCode, Integer rangeStart, Integer rangeEnd, Double price, Integer leadTime, String currency) {
        this.itemPriceId = itemPriceId;
        this.vendorCode = vendorCode;
        this.itemCode = itemCode;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.price = price;
        this.leadTime = leadTime;
        this.currency = currency;
    }

    public MoqDTO(Integer itemPriceId, String vendorCode, String itemCode, Integer rangeStart, Integer rangeEnd, Double price, Integer leadTime, String currency, Date dueDate, String note) {
        this.itemPriceId = itemPriceId;
        this.vendorCode = vendorCode;
        this.itemCode = itemCode;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.price = price;
        this.leadTime = leadTime;
        this.currency = currency;
        this.dueDate = dueDate;
        this.note = note;
    }

    public Integer getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(Integer rangeStart) {
        this.rangeStart = rangeStart;
    }

    public Integer getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(Integer rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
