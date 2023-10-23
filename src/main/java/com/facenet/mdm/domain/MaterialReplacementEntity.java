package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "material_replacement")
@IdClass(MaterialReplacementEntityPK.class)
public class MaterialReplacementEntity {
    @Id
    @Column(name = "material_code")
    private String materialCode;
    @Id
    @Column(name = "material_replacement_code")
    private String materialReplacementCode;

    public MaterialReplacementEntity() {
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialReplacementCode() {
        return materialReplacementCode;
    }

    public void setMaterialReplacementCode(String materialReplacementCode) {
        this.materialReplacementCode = materialReplacementCode;
    }

    public MaterialReplacementEntity(String materialCode, String materialReplacementCode) {
        this.materialCode = materialCode;
        this.materialReplacementCode = materialReplacementCode;
    }
}
