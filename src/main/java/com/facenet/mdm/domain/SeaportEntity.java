package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "seaport")
public class SeaportEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seaport_code")
    private String seaportCode;

    @Column(name = "seaport_name")
    private String seaportName;

    @Column(name = "seaport_nation")
    private String seaportNation;

    @Column(name = "seaport_address")
    private String seaportAddress;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "note")
    private String note;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_active")
    private boolean isActive;

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeaportCode() {
        return seaportCode;
    }

    public void setSeaportCode(String seaportCode) {
        this.seaportCode = seaportCode;
    }

    public String getSeaportName() {
        return seaportName;
    }

    public void setSeaportName(String seaportName) {
        this.seaportName = seaportName;
    }

    public String getSeaportNation() {
        return seaportNation;
    }

    public void setSeaportNation(String seaportNation) {
        this.seaportNation = seaportNation;
    }

    public String getSeaportAddress() {
        return seaportAddress;
    }

    public void setSeaportAddress(String seaportAddress) {
        this.seaportAddress = seaportAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
