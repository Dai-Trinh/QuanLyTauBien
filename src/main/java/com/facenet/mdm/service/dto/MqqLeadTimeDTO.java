package com.facenet.mdm.service.dto;

import java.util.Date;
import java.util.List;

public class MqqLeadTimeDTO {

    private Long leadTimeId;
    private Integer leadTime;
    private Date dueDate;
    private String note;
    private List<MqqPriceDTO> mqqPriceList;

    public Long getLeadTimeId() {
        return leadTimeId;
    }

    public void setLeadTimeId(Long leadTimeId) {
        this.leadTimeId = leadTimeId;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public List<MqqPriceDTO> getMqqPriceList() {
        return mqqPriceList;
    }

    public void setMqqPriceList(List<MqqPriceDTO> mqqPriceList) {
        this.mqqPriceList = mqqPriceList;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
