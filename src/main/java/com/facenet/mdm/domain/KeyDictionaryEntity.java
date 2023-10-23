package com.facenet.mdm.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "key_dictionary")
public class KeyDictionaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GenericGenerator(name = "key_id_gen", strategy = "increment")
    @Column(name = "key_id", nullable = false)
    private Integer id;
    @Size(max = 255)
    @Column(name = "key_name")
    private String keyName;
    @Size(max = 255)
    @Column(name = "key_title")
    private String keyTitle;
    @Column(name = "is_active")
    private Boolean isActive = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
