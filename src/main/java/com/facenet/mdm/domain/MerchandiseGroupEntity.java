package com.facenet.mdm.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "merchandise_group")
public class MerchandiseGroupEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer merchandiseGroupId;

    @Column(name = "merchandise_group_code", length = 50)
    private String merchandiseGroupCode;

    @Column(name = "merchandise_group_name")
    private String merchandiseGroupName;

    @Column(name = "merchandise_group_description")
    private String merchandiseGroupDescription;

    @Column(name = "merchandise_group_note")
    private String merchandiseGroupNote;

    @Column(name = "merchandise_group_status")
    private Integer merchandiseGroupStatus;

    @OneToMany(mappedBy = "merchandiseGroupEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CoittEntity> coittEntities = new HashSet<>();

    @OneToMany(mappedBy = "merchandiseGroupEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Citt1Entity> citt1Entities = new HashSet<>();

    @Column(name = "is_active")
    private boolean isActive;

    public MerchandiseGroupEntity() {}

    public MerchandiseGroupEntity(MerchandiseGroupEntity that) {
        this.merchandiseGroupCode = that.merchandiseGroupCode;
        this.merchandiseGroupName = that.merchandiseGroupName;
        this.merchandiseGroupDescription = that.merchandiseGroupDescription;
        this.merchandiseGroupNote = that.merchandiseGroupNote;
        this.merchandiseGroupStatus = that.merchandiseGroupStatus;
        this.coittEntities = that.coittEntities;
    }

    @Override
    public Integer getId() {
        return merchandiseGroupId;
    }

    public Integer getMerchandiseGroupId() {
        return merchandiseGroupId;
    }

    public void setMerchandiseGroupId(Integer merchandiseGroupId) {
        this.merchandiseGroupId = merchandiseGroupId;
    }

    public String getMerchandiseGroupCode() {
        return merchandiseGroupCode;
    }

    public void setMerchandiseGroupCode(String merchandiseGroupCode) {
        this.merchandiseGroupCode = merchandiseGroupCode;
    }

    public String getMerchandiseGroupName() {
        return merchandiseGroupName;
    }

    public void setMerchandiseGroupName(String merchandiseGroupName) {
        this.merchandiseGroupName = merchandiseGroupName;
    }

    public String getMerchandiseGroupDescription() {
        return merchandiseGroupDescription;
    }

    public void setMerchandiseGroupDescription(String merchandiseGroupDescription) {
        this.merchandiseGroupDescription = merchandiseGroupDescription;
    }

    public String getMerchandiseGroupNote() {
        return merchandiseGroupNote;
    }

    public void setMerchandiseGroupNote(String merchandiseGroupNote) {
        this.merchandiseGroupNote = merchandiseGroupNote;
    }

    public Integer getMerchandiseGroupStatus() {
        return merchandiseGroupStatus;
    }

    public void setMerchandiseGroupStatus(Integer merchandiseGroupStatus) {
        this.merchandiseGroupStatus = merchandiseGroupStatus;
    }

    public Set<CoittEntity> getMerchandiseEntitySet() {
        return coittEntities;
    }

    public void setMerchandiseEntitySet(Set<CoittEntity> coittEntities) {
        this.coittEntities = coittEntities;
    }

    public Set<CoittEntity> getCoittEntities() {
        return coittEntities;
    }

    public void setCoittEntities(Set<CoittEntity> coittEntities) {
        this.coittEntities = coittEntities;
    }

    public Set<Citt1Entity> getCitt1Entities() {
        return citt1Entities;
    }

    public void setCitt1Entities(Set<Citt1Entity> citt1Entities) {
        this.citt1Entities = citt1Entities;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
