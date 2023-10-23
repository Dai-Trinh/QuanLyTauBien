package com.facenet.mdm.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class KeyValueEntityId implements Serializable {
    private static final long serialVersionUID = 7934237401305697427L;
    @NotNull
    @Column(name = "key_value_id", nullable = false)
    private Integer keyValueId;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @NotNull
    @Column(name = "entity_key", nullable = false)
    private Integer entityKey;

    public KeyValueEntityId() {
    }

    public Integer getKeyValueId() {
        return keyValueId;
    }

    public void setKeyValueId(Integer keyValueId) {
        this.keyValueId = keyValueId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(Integer entityKey) {
        this.entityKey = entityKey;
    }

    public KeyValueEntityId(@NotNull Integer keyValueId, @NotNull Integer entityId, @NotNull Integer entityValue) {
        this.keyValueId = keyValueId;
        this.entityId = entityId;
        this.entityKey = entityValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        KeyValueEntityId entity = (KeyValueEntityId) o;
        return Objects.equals(this.keyValueId, entity.keyValueId) &&
                Objects.equals(this.entityId, entity.entityId)&&
                Objects.equals(this.entityKey, entity.entityKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyValueId, entityId, entityKey);
    }

}
