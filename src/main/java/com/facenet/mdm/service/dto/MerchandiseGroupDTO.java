package com.facenet.mdm.service.dto;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;

public class MerchandiseGroupDTO extends BaseDynamicDTO implements Serializable {

    private String merchandiseGroupCode;

    private String merchandiseGroupName;

    private String merchandiseGroupDescription;

    private String merchandiseGroupNote;

    private Integer merchandiseGroupStatus;

    private List<CoittDTO> coittDTOList;

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

    public List<CoittDTO> getCoittDTOList() {
        return coittDTOList;
    }

    public void setCoittDTOList(List<CoittDTO> coittDTOList) {
        this.coittDTOList = coittDTOList;
    }
}
