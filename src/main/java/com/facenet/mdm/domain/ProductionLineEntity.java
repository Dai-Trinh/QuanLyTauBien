package com.facenet.mdm.domain;

import com.facenet.mdm.service.utils.Contants;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "production_line")
public class ProductionLineEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @SequenceGenerator(name = "production_line_id_gen", sequenceName = "sequence_generator")
    @Column(name = "production_line_id")
    private Integer productionLineId;

    @Column(name = "production_line_code")
    private String productionLineCode;

    @Column(name = "production_line_name")
    private String productionLineName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "production_line_type")
    private ProductionLineTypeEntity productionLineType;

    @Column(name = "productivity")
    private Double productivity;

    @Column(name = "description")
    private String description;

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

    @Column(name = "status")
    private Integer status = Contants.MachineStatus.ACTIVE;

    @Column(name = "cycle_time")
    private Double cycleTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public ProductionLineEntity(ProductionLineEntity productionLineEntity) {
        this.productionLineId = productionLineEntity.productionLineId;
        this.productionLineCode = productionLineEntity.productionLineCode;
        this.productionLineName = productionLineEntity.productionLineName;
        this.productionLineType = productionLineEntity.productionLineType;
        this.productivity = productionLineEntity.productivity;
        this.description = productionLineEntity.description;
        this.supplier = productionLineEntity.supplier;
        this.maintenanceTime = productionLineEntity.maintenanceTime;
        this.maintenanceTimeUnit = productionLineEntity.maintenanceTimeUnit;
        this.minProductionQuantity = productionLineEntity.minProductionQuantity;
        this.maxProductionQuantity = productionLineEntity.maxProductionQuantity;
        this.purchaseDate = productionLineEntity.purchaseDate;
        this.maxWaitingTime = productionLineEntity.maxWaitingTime;
        this.maxWaitingTimeUnit = productionLineEntity.maxWaitingTimeUnit;
        this.status = productionLineEntity.status;
        this.cycleTime = productionLineEntity.cycleTime;
        this.isActive = productionLineEntity.isActive;
    }

    public ProductionLineEntity() {}

    public ProductionLineTypeEntity getProductionLineType() {
        return productionLineType;
    }

    public void setProductionLineType(ProductionLineTypeEntity productionLineTypeEntity) {
        this.productionLineType = productionLineTypeEntity;
    }

    @Override
    public Integer getId() {
        return productionLineId;
    }

    public Integer getProductionLineId() {
        return productionLineId;
    }

    public void setProductionLineId(Integer productionLineId) {
        this.productionLineId = productionLineId;
    }

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

    //    public String getProductionLineType() {
    //        return productionLineType;
    //    }
    //
    //    public void setProductionLineType(String productionLineType) {
    //        this.productionLineType = productionLineType;
    //    }

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

    public Double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Double cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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
