package com.facenet.mdm.domain;

import com.facenet.mdm.domain.VendorItemEntityPK;
import javax.persistence.*;

@Entity
@Table(name = "link_vendor_coitt")
@IdClass(VendorItemEntityPK.class)
public class VendorItemEntity {

    @Id
    @Column(name = "vendor_code")
    private String vendorCode;

    @Id
    @Column(name = "item_code")
    private String itemCode;

    public VendorItemEntity() {}

    public VendorItemEntity(String vendorCode, String itemCode) {
        this.vendorCode = vendorCode;
        this.itemCode = itemCode;
    }

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
}
