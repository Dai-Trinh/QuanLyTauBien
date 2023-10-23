package com.facenet.mdm.service.model;

import com.facenet.mdm.domain.*;

import java.util.*;

public class MqqPriceExcelModel {
    List<MqqPriceEntity> mqqPriceEntities = new ArrayList<>();
    List<LeadTimeEntity> leadTimeEntities = new ArrayList<>();
//    Map<String, SaleEntity> saleEntityMap = new HashMap<>();
    Set<String> vendorCodes = new HashSet<>();
    List<CoittEntity> itemEntities = new ArrayList<>();
    List<VendorEntity> vendorEntities = new ArrayList<>();

    public List<CoittEntity> getItemEntities() {
        return itemEntities;
    }

    public void setItemEntities(List<CoittEntity> itemEntities) {
        this.itemEntities = itemEntities;
    }

    public List<VendorEntity> getVendorEntities() {
        return vendorEntities;
    }

    public void setVendorEntities(List<VendorEntity> vendorEntities) {
        this.vendorEntities = vendorEntities;
    }

    public Set<String> getVendorCodes() {
        return vendorCodes;
    }

    public void setVendorCodes(Set<String> vendorCodes) {
        this.vendorCodes = vendorCodes;
    }

    public List<MqqPriceEntity> getMqqPriceEntities() {
        return mqqPriceEntities;
    }

    public void setMqqPriceEntities(List<MqqPriceEntity> mqqPriceEntities) {
        this.mqqPriceEntities = mqqPriceEntities;
    }

    public List<LeadTimeEntity> getLeadTimeEntities() {
        return leadTimeEntities;
    }

    public void setLeadTimeEntities(List<LeadTimeEntity> leadTimeEntities) {
        this.leadTimeEntities = leadTimeEntities;
    }

//    public Map<String, SaleEntity> getSaleEntityMap() {
//        return saleEntityMap;
//    }

//    public void setSaleEntityMap(Map<String, SaleEntity> saleEntityMap) {
//        this.saleEntityMap = saleEntityMap;
//    }
}
