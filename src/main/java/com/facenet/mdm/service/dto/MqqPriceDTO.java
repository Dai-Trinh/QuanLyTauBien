package com.facenet.mdm.service.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.util.Date;

public class MqqPriceDTO {

    private Integer mqqPriceId;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Double priceMQQ;
    private String transactionMoney;
    private Date startDate;
    private Date dueDate;
    private String note;

    public Integer getMqqPriceId() {
        return mqqPriceId;
    }

    public void setMqqPriceId(Integer mqqPriceId) {
        this.mqqPriceId = mqqPriceId;
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

    public Double getPriceMQQ() {
        return priceMQQ;
    }

    public void setPriceMQQ(Double priceMQQ) {
        this.priceMQQ = priceMQQ;
    }

    public String getTransactionMoney() {
        return transactionMoney;
    }

    public void setTransactionMoney(String transactionMoney) {
        this.transactionMoney = transactionMoney;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public MqqPriceDTO() {
    }

    @QueryProjection
    public MqqPriceDTO(Integer mqqPriceId, Integer minQuantity, Integer maxQuantity, Double priceMQQ, String transactionMoney, Date dueDate, Date startDate, String note) {
        this.mqqPriceId = mqqPriceId;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.priceMQQ = priceMQQ;
        this.transactionMoney = transactionMoney;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.note = note;
    }
}
