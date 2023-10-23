package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Column;

public class CoittDTO extends BaseDynamicDTO implements Serializable {

    private String productCode;
    private String proName;
    private String proParent;
    private String techName;
    private Integer itemGroupCode;
    private String unit;
    private String version;
    private String note;
    private String notice;
    private Boolean isTemplate;
    private Double quantity;
    private Integer parent;
    private Integer status;
    private String kind;

    private String proDesc;
    private String merchandiseGroup;

    private String wareHouse;

    private String vendor;
    private List<CoittDTO> coittDTOS;

    private String oldVersion;

    private List<String> materialReplaceCode;

    private List<String> materialReplaceName;

    @JsonIgnore
    private Instant createdAt;

    public CoittDTO(
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
        String kind
    ) {
        this.productCode = productCode;
        this.proName = proName;
        this.techName = techName;
        this.itemGroupCode = itemGroupCode;
        this.unit = unit;
        this.version = version;
        this.note = note;
        this.notice = notice;
        this.quantity = quantity;
        this.parent = parent;
        this.status = status;
        this.kind = kind;
    }

    public CoittDTO(
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
        String kind
    ) {
        this.productCode = productCode;
        this.proName = proName;
        this.techName = techName;
        this.itemGroupCode = itemGroupCode;
        this.unit = unit;
        this.version = version;
        this.note = note;
        this.notice = notice;
        this.quantity = quantity;
        this.status = status;
        this.kind = kind;
    }

    public CoittDTO(String productCode, String proName, String techName, String unit, String version, String note) {
        this.productCode = productCode;
        this.proName = proName;
        this.techName = techName;
        this.unit = unit;
        this.version = version;
        this.note = note;
    }

    //    private final Map<String, String> map = new HashMap<>();

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

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
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

    public String getTechName() {
        return techName;
    }

    public String getProDesc() {
        return proDesc;
    }

    public void setProDesc(String proDesc) {
        this.proDesc = proDesc;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
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

    public String getMerchandiseGroup() {
        return merchandiseGroup;
    }

    public void setMerchandiseGroup(String merchandiseGroup) {
        this.merchandiseGroup = merchandiseGroup;
    }

    public List<CoittDTO> getCoittDTOS() {
        return coittDTOS;
    }

    public void setCoittDTOS(List<CoittDTO> coittDTOS) {
        this.coittDTOS = coittDTOS;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }

    public String getProParent() {
        return proParent;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public void setProParent(String proParent) {
        this.proParent = proParent;
    }

    public CoittDTO(
        String productCode,
        String proName,
        String techName,
        Integer itemGroupCode,
        String unit,
        String version,
        String note,
        String notice,
        Boolean isTemplate,
        Double quantity,
        Integer status,
        String kind,
        String wareHouse
    ) {
        this.productCode = productCode;
        this.proName = proName;
        this.techName = techName;
        this.itemGroupCode = itemGroupCode;
        this.unit = unit;
        this.version = version;
        this.note = note;
        this.notice = notice;
        this.isTemplate = isTemplate;
        this.quantity = quantity;

        this.status = status;
        this.kind = kind;

        this.wareHouse = wareHouse;
    }

    public List<String> getMaterialReplaceCode() {
        return materialReplaceCode;
    }

    public void setMaterialReplaceCode(List<String> materialReplaceCode) {
        this.materialReplaceCode = materialReplaceCode;
    }

    public List<String> getMaterialReplaceName() {
        return materialReplaceName;
    }

    public void setMaterialReplaceName(List<String> materialReplaceName) {
        this.materialReplaceName = materialReplaceName;
    }

    public CoittDTO() {}
    //    @JsonAnyGetter
    //    public Map<String, String> getMap() {
    //        return map;
    //    }
    //    @JsonAnySetter
    //    public void setMap(String key, String value) {
    //        map.put(key, value);
    //    }
}
