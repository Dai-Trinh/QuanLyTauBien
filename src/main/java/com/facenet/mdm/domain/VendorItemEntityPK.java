package com.facenet.mdm.domain;

import java.io.Serializable;

public class VendorItemEntityPK implements Serializable {
    private String vendorCode;
    private String itemCode;

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VendorItemEntityPK that = (VendorItemEntityPK) o;

        if (vendorCode != null ? !vendorCode.equals(that.vendorCode) : that.vendorCode != null) return false;
        if (itemCode != null ? !itemCode.equals(that.itemCode) : that.itemCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = vendorCode != null ? vendorCode.hashCode() : 0;
        result = 31 * result + (itemCode != null ? itemCode.hashCode() : 0);
        return result;
    }
}
