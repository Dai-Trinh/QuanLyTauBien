package com.facenet.mdm.domain;

import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "machine")
public class MachineEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @GenericGenerator(name = "machine_id_gen", strategy = "increment")
    @Column(name = "machine_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "machine_code")
    private String machineCode;

    @Size(max = 255)
    @Column(name = "machine_name")
    private String machineName;

    //    @Column(name = "machine_type", insertable = false, updatable = false)
    //    private Integer machineType;


    @Column(name = "productivity")
    private Double productivity;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "supplier")
    private String supplier;

    @Column(name = "maintenance_time")
    private Double maintenanceTime;

    @Column(name = "maintenance_time_unit")
    private Integer maintenanceTimeUnit;

    @Column(name = "min_production_quantity")
    private Double minProductionQuantity;

    @Column(name = "max_production_quantity")
    private Double maxProductionQuantity;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "max_waiting_time")
    private Double maxWaitingTime;

    @Column(name = "max_waiting_time_unit")
    private Integer maxWaitingTimeUnit;

    @Column(name = "cycle_time")
    private Double cycleTime;

    @Column(name = "status")
    private Integer status = Contants.MachineStatus.ACTIVE;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public MachineEntity() {}

    public MachineEntity(MachineEntity that) {
        this.id = that.id;
        this.machineCode = that.machineCode;
        this.machineName = that.machineName;
        this.productivity = that.productivity;
        this.description = that.description;
        this.supplier = that.supplier;
        this.maintenanceTime = that.maintenanceTime;
        this.minProductionQuantity = that.minProductionQuantity;
        this.maxProductionQuantity = that.maxProductionQuantity;
        this.purchaseDate = that.purchaseDate;
        this.maxWaitingTime = that.maxWaitingTime;
        this.cycleTime = that.cycleTime;
        this.status = that.status;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        if (status == null) {
            this.status = 1;
        } else {
            this.status = status;
        }
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

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<BusinessLogDetailEntity> toLogEntity() throws IllegalAccessException {
        List<BusinessLogDetailEntity> businessLogDetailEntities = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity();
            if (field.get(this) != null) {
                businessLogDetailEntity.setKeyName(field.getName());
                businessLogDetailEntity.setNewValue(field.get(this).toString());
            }
            businessLogDetailEntities.add(businessLogDetailEntity);
        }
        return businessLogDetailEntities;
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

    public void setMaintenanceTimeUnitConvertString(String maintenanceTimeUnitString){
        switch (maintenanceTimeUnitString.toLowerCase()){
            case "giây":
                this.maintenanceTimeUnit = 0;
                break;
            case "phút":
                this.maintenanceTimeUnit = 1;
                break;
            case "giờ":
                this.maintenanceTimeUnit = 2;
                break;
            case "ngày":
                this.maintenanceTimeUnit = 3;
                break;
            case "tháng":
                this.maintenanceTimeUnit = 4;
                break;
        }
    }
    public void setMaxWaitingTimeUnitConvertString(String maxWaitingTimeUnitString){
        switch (maxWaitingTimeUnitString.toLowerCase()){
            case "giây":
                this.maxWaitingTimeUnit = 0;
                break;
            case "phút":
                this.maxWaitingTimeUnit = 1;
                break;
            case "giờ":
                this.maxWaitingTimeUnit = 2;
                break;
            case "ngày":
                this.maxWaitingTimeUnit = 3;
                break;
            case "tháng":
                this.maxWaitingTimeUnit = 4;
                break;
        }
    }
}
