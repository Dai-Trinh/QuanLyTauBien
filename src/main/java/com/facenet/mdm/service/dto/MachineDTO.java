package com.facenet.mdm.service.dto;

import com.facenet.mdm.domain.MachineEntity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link MachineEntity} entity
 */
public class MachineDTO extends BaseDynamicDTO implements Serializable {

    private String machineCode;
    private String machineName;
    private Double productivity;
    private String description;
    private Integer status;
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
    private Double cycleTime;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getPurchaseDateStart() {
        return purchaseDateStart;
    }

    public LocalDate getPurchaseDateEnd() {
        return purchaseDateEnd;
    }

    public void setPurchaseDateEnd(LocalDate purchaseDateEnd) {
        this.purchaseDateEnd = purchaseDateEnd;
    }

    public void setPurchaseDateStart(LocalDate purchaseDateStart) {
        this.purchaseDateStart = purchaseDateStart;
    }

    public Double getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(Double maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
