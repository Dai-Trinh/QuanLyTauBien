package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "production_stage")
public class ProductionStageEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @GenericGenerator(name = "machine_id_gen", strategy = "increment")
    @Column(name = "production_stage_id", nullable = false)
    private Integer id;

    @Column(name = "production_stage_code")
    private String productionStageCode;

    @Column(name = "production_stage_name")
    private String productionStageName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public ProductionStageEntity() {}

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductionStageCode() {
        return productionStageCode;
    }

    public void setProductionStageCode(String productionStageCode) {
        this.productionStageCode = productionStageCode;
    }

    public String getProductionStageName() {
        return productionStageName;
    }

    public void setProductionStageName(String productionStageName) {
        this.productionStageName = productionStageName;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ProductionStageEntity(String productionStageCode, String productionStageName, String description, Integer status) {
        this.productionStageCode = productionStageCode;
        this.productionStageName = productionStageName;
        this.description = description;
        this.status = status;
    }

    public ProductionStageEntity(ProductionStageEntity that) {
        this.id = that.id;
        this.productionStageCode = that.productionStageCode;
        this.productionStageName = that.productionStageName;
        this.description = that.description;
        this.status = that.status;
    }
}
