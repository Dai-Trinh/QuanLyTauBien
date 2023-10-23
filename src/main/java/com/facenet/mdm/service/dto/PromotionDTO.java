package com.facenet.mdm.service.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PromotionDTO implements Serializable {

    private Date startDate;
    private Date endDate;

    private String note;
    private List<PriceListDTO> priceList;

    public PromotionDTO(Date startDate, Date endDate, String note) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.note = note;
    }

    public PromotionDTO() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<PriceListDTO> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceListDTO> priceList) {
        this.priceList = priceList;
    }
}
