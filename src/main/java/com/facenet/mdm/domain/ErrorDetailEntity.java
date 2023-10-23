package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "link_error_error_group")
public class ErrorDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "error_detail_id")
    private Integer errorDetailId;

    @Column(name = "error_id")
    private Integer errorId;

    @Column(name = "error_group_id")
    private Integer errorGroupId;

    public Integer getErrorDetailId() {
        return errorDetailId;
    }

    public void setErrorDetailId(Integer errorDetailId) {
        this.errorDetailId = errorDetailId;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public Integer getErrorGroupId() {
        return errorGroupId;
    }

    public void setErrorGroupId(Integer errorGroupId) {
        this.errorGroupId = errorGroupId;
    }
}
