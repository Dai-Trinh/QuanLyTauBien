package com.facenet.mdm.domain;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "citt1")
public class Citt1Entity extends AbstractAuditingEntity<Integer> {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "CITT1_id")
    private Integer id;

    @Basic
    @Column(name = "material_code")
    private String materialCode;

    @Basic
    @Column(name = "products_code")
    private String productCode;

    @Basic
    @Column(name = "pro_name")
    private String proName;

    @Basic
    @Column(name = "tech_name")
    private String techName;

    @Basic
    @Column(name = "item_group_code")
    private Integer itemGroupCode;

    @Basic
    @Column(name = "production_norm")
    private Double productionNorm;

    @Basic
    @Column(name = "unit")
    private String unit;

    @Basic
    @Column(name = "kind")
    private String kind;

    @Basic
    @Column(name = "version")
    private String version;

    @Basic
    @Column(name = "note")
    private String note;

    @Basic
    @Column(name = "notice")
    private String notice;

    @Basic
    @Column(name = "is_template")
    private Boolean isTemplate;

    @Basic
    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "ware_house")
    private String wareHouse;

    @Basic
    @Column(name = "status")
    private Integer status;

    @Column(name = "material_replace_code")
    private String materialReplaceCode;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "merchandise_group_id")
    private MerchandiseGroupEntity merchandiseGroupEntity;

    @ManyToOne
    @JoinColumn(name = "coitt_id")
    private CoittEntity coittEntity;

    @Column(name = "vendor")
    private String vendor;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getProductionNorm() {
        return productionNorm;
    }

    public void setProductionNorm(Double productionNorm) {
        this.productionNorm = productionNorm;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Integer getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(Integer itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(Boolean template) {
        isTemplate = template;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public MerchandiseGroupEntity getMerchandiseGroupEntity() {
        return merchandiseGroupEntity;
    }

    public void setMerchandiseGroupEntity(MerchandiseGroupEntity merchandiseGroupEntity) {
        this.merchandiseGroupEntity = merchandiseGroupEntity;
    }

    public String getMaterialReplaceCode() {
        return materialReplaceCode;
    }

    public void setMaterialReplaceCode(String materialReplaceCode) {
        this.materialReplaceCode = materialReplaceCode;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }

    public CoittEntity getCoittEntity() {
        return coittEntity;
    }

    public void setCoittEntity(CoittEntity coittEntity) {
        this.coittEntity = coittEntity;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Citt1Entity() {}

    public Citt1Entity(Citt1Entity that) {
        this.id = that.id;
        this.materialCode = that.materialCode;
        this.productCode = that.productCode;
        this.proName = that.proName;
        this.techName = that.techName;
        this.itemGroupCode = that.itemGroupCode;
        this.unit = that.unit;
        this.kind = that.kind;
        this.version = that.version;
        this.note = that.note;
        this.notice = that.notice;
        this.isTemplate = that.isTemplate;
        this.quantity = that.quantity;
        this.status = that.status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Citt1Entity that = (Citt1Entity) o;

        if (id != that.id) return false;
        if (materialCode != null ? !materialCode.equals(that.materialCode) : that.materialCode != null) return false;
        if (productCode != null ? !productCode.equals(that.productCode) : that.productCode != null) return false;
        if (proName != null ? !proName.equals(that.proName) : that.proName != null) return false;
        if (itemGroupCode != null ? !itemGroupCode.equals(that.itemGroupCode) : that.itemGroupCode != null) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (notice != null ? !notice.equals(that.notice) : that.notice != null) return false;
        if (isTemplate != null ? !isTemplate.equals(that.isTemplate) : that.isTemplate != null) return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (materialCode != null ? materialCode.hashCode() : 0);
        result = 31 * result + (productCode != null ? productCode.hashCode() : 0);
        result = 31 * result + (proName != null ? proName.hashCode() : 0);
        result = 31 * result + (itemGroupCode != null ? itemGroupCode.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (notice != null ? notice.hashCode() : 0);
        result = 31 * result + (isTemplate != null ? isTemplate.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
