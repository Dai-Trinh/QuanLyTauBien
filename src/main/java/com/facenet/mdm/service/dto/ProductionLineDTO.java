package com.facenet.mdm.service.dto;

import com.facenet.mdm.domain.ProductionLineTypeEntity;
import java.io.Serializable;
import java.time.LocalDate;

public class ProductionLineDTO extends BaseDynamicDTO implements Serializable {

    private String productionLineCode;
    private String productionLineName;
    private ProductionLineTypeEntity productionLineType;
    private Double productivity;
    private String description;
    private String supplier;
    private Double maintenanceTime;
    private Integer maintenanceTimeUnit;
    private Double minProductionQuantity;
    private Double maxProductionQuantity;
    private LocalDate purchaseDate;

    private LocalDate purchaseDateStart;
    private LocalDate purchaseDateEnd;
    private Double maxWaitingTime;

    private Integer maxWaitingTimeUnit;
    private Integer status;
    private Double cycleTime;

    public String getProductionLineCode() {
        return productionLineCode;
    }

    public void setProductionLineCode(String productionLineCode) {
        this.productionLineCode = productionLineCode;
    }

    public String getProductionLineName() {
        return productionLineName;
    }

    public void setProductionLineName(String productionLineName) {
        this.productionLineName = productionLineName;
    }

    public ProductionLineTypeEntity getProductionLineType() {
        return productionLineType;
    }

    public void setProductionLineType(ProductionLineTypeEntity productionLineType) {
        this.productionLineType = productionLineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(Double maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public Double getMinProductionQuantity() {
        return minProductionQuantity;
    }

    public void setMinProductionQuantity(Double minProductionQuantity) {
        this.minProductionQuantity = minProductionQuantity;
    }

    public Double getMaxProductionQuantity() {
        return maxProductionQuantity;
    }

    public void setMaxProductionQuantity(Double maxProductionQuantity) {
        this.maxProductionQuantity = maxProductionQuantity;
    }

    public Double getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(Double maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getPurchaseDateStart() {
        return purchaseDateStart;
    }

    public void setPurchaseDateStart(LocalDate purchaseDateStart) {
        this.purchaseDateStart = purchaseDateStart;
    }

    public LocalDate getPurchaseDateEnd() {
        return purchaseDateEnd;
    }

    public void setPurchaseDateEnd(LocalDate purchaseDateEnd) {
        this.purchaseDateEnd = purchaseDateEnd;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Integer getMaintenanceTimeUnit() {
        return maintenanceTimeUnit;
    }

    public void setMaintenanceTimeUnit(Integer maintenanceTimeUnit) {
        this.maintenanceTimeUnit = maintenanceTimeUnit;
    }

    public Integer getMaxWaitingTimeUnit() {
        return maxWaitingTimeUnit;
    }

    public void setMaxWaitingTimeUnit(Integer maxWaitingTimeUnit) {
        this.maxWaitingTimeUnit = maxWaitingTimeUnit;
    }
}
