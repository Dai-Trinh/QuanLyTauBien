package com.facenet.mdm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "production_line_type")
public class ProductionLineTypeEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "production_line_type_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "production_line_type_name", nullable = false)
    private String productionLineTypeName;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductionLineTypeName() {
        return productionLineTypeName;
    }

    public void setProductionLineTypeName(String productionLineTypeName) {
        this.productionLineTypeName = productionLineTypeName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
