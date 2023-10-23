package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import java.time.Instant;
import java.util.Date;

public class DataItemInVendor extends CoittDTO {

    private String productCode;
    private String groupName;
    private String productType;
    private Integer leadTime;
    private Double priceMQQ;
    private Date dueDate;
    private String currency;
    private String leadTimeNote;

    @JsonIgnore
    private Instant createdAt;

    public DataItemInVendor(
        String productCode,
        String proName,
        String techName,
        Integer itemGroupCode,
        String unit,
        String version,
        String note,
        String notice,
        Double quantity,
        Integer parent,
        Integer status,
        String kind,
        Instant createdAt
    ) {
        super(productCode, proName, techName, itemGroupCode, unit, version, note, notice, quantity, parent, status, kind);
        this.productCode = productCode;
        this.createdAt = createdAt;
    }

    public DataItemInVendor(
        String productCode,
        String proName,
        String techName,
        Integer itemGroupCode,
        String unit,
        String version,
        String note,
        String notice,
        Double quantity,
        Integer status,
        String kind,
        Instant createdAt
    ) {
        super(productCode, proName, techName, itemGroupCode, unit, version, note, notice, quantity, status, kind);
        this.productCode = productCode;
        this.createdAt = createdAt;
    }

    public String getLeadTimeNote() {
        return leadTimeNote;
    }

    public void setLeadTimeNote(String leadTimeNote) {
        this.leadTimeNote = leadTimeNote;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Double getPriceMQQ() {
        return priceMQQ;
    }

    public void setPriceMQQ(Double priceMQQ) {
        this.priceMQQ = priceMQQ;
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

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public DataItemInVendor() {}

    @QueryProjection
    public DataItemInVendor(String productCode) {
        this.productCode = productCode;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
