package com.facenet.mdm.domain;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "leadtime")
public class LeadTimeEntity extends AbstractAuditingEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leadtime_id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "vendor_code", nullable = false, length = 20)
    private String vendorCode;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "leadtime")
    private Integer leadTime;
    @Column(name = "moq_price_min")
    private Double moqPriceMin;
    @Column(name = "currency")
    private String currency;

    @Formula(value = "(select MIN(m.price) from mqq_price as m join leadtime as l on m.vendor_code = l.vendor_code where m.vendor_code = vendor_code and m.item_code = item_code and m.is_promotion = 0 and m.is_active=1)")
    private Double mqqPriceMin;

//    @Formula(value = "(select m.currency from mqq_price as m join leadtime as l on m.vendor_code = l.vendor_code where m.vendor_code = vendor_code and m.item_code = item_code and m.is_promotion = 0 and m.is_active=1 order by MIN(m.price))")
//    private String currency;

    @Formula(value = "(select m.time_end from mqq_price as m join leadtime as l on m.vendor_code = l.vendor_code where m.vendor_code = vendor_code and m.item_code = item_code and m.is_active = 1 limit 1 )")
    private Date timeEnd;

    @Column(name = "note")
    private String note;

    @Column(name = "is_active", nullable = false)
    private Byte isActive = 1;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public Double getMqqPriceMin() {
        return mqqPriceMin;
    }

    public void setMqqPriceMin(Double mqqPriceMin) {
        this.mqqPriceMin = mqqPriceMin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    public Double getMoqPriceMin() {
        return moqPriceMin;
    }

    public void setMoqPriceMin(Double moqPriceMin) {
        this.moqPriceMin = moqPriceMin;
    }
}
