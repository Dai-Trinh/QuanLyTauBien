package com.facenet.mdm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "column_properties")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ColumnPropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @GenericGenerator(name = "column_properties_id_gen", strategy = "increment")
    @Column(name = "column_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "key_name")
    private String keyName;

    @Size(max = 255)
    @Column(name = "key_title")
    private String keyTitle;

    @Size(max = 20)
    @Column(name = "width", length = 20)
    private String width;

    @Column(name = "entity_type")
    private Integer entityType;

    @Column(name = "is_visible")
    private Boolean check;

    @Column(name = "data_type")
    private Integer dataType;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive = true;

    @Column(name = "entry_index")
    @JsonProperty("entryIndex")
    private Integer index;

    @Column(name = "is_fixed")
    private Integer isFixed = 0;

    @Column(name = "is_required")
    private Boolean isRequired;

    public String getDataTypeConvertString() {
        switch (dataType) {
            case 1:
                return "Integer";
            case 2:
                return "Float";
            case 3:
                return "String";
            case 4:
                return "Json";
            case 5:
                return "Date";
            case 6:
                return "Boolean";
            default:
                return null;
        }
    }

    public String getCheckConvertString() {
        if (check != null && check) {
            return "Hiển thị";
        } else {
            return "Không hiển thị";
        }
    }

    public String getIsRequiredConvertString() {
        if (isRequired != null && isRequired) {
            return "Bắt buộc";
        } else {
            return "Không bắt buộc";
        }
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean required) {
        isRequired = required;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Integer isFixed) {
        this.isFixed = isFixed;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }
}
