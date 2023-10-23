package com.facenet.mdm.domain;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "business_log_detail")
@EntityListeners(AuditingEntityListener.class)
public class BusinessLogDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blSeqGen")
    @GenericGenerator(name = "blSeqGen", strategy = "increment")
    @Column(name = "business_log_detail_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_log_id", nullable = false)
    private BusinessLogEntity businessLog;

    @Size(max = 255)
    @NotNull
    @Column(name = "key_name", nullable = false)
    private String keyName;

    @Size(max = 500)
    @Column(name = "last_value")
    private String lastValue;

    @Size(max = 500)
    @Column(name = "new_value")
    private String newValue;

    @Column(name = "created_at")
    @CreatedDate
    private Instant createdAt = Instant.now();

    public BusinessLogDetailEntity newValue(String newValue) {
        this.newValue = newValue;
        return this;
    }

    public BusinessLogDetailEntity lastValue(String lastValue) {
        this.lastValue = lastValue;
        return this;
    }

    public BusinessLogDetailEntity keyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessLogEntity getBusinessLog() {
        return businessLog;
    }

    public void setBusinessLog(BusinessLogEntity businessLog) {
        this.businessLog = businessLog;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BusinessLogDetailEntity that = (BusinessLogDetailEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
