package com.facenet.mdm.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "mqq_price")
public class MqqPriceEntity extends AbstractAuditingEntity<Integer> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "item_price_id", nullable = false)
    private Integer id;

    @Column(name = "vendor_code", nullable = true, length = 20)
    private String vendorCode;

    @Column(name = "item_code", nullable = true, length = 20)
    private String itemCode;

    @Column(name = "time_start", nullable = true)
    private Date timeStart;

    @Column(name = "time_end", nullable = true)
    private Date timeEnd;

    @Column(name = "range_start", nullable = true)
    private Integer rangeStart;

    @Column(name = "range_end", nullable = true)
    private Integer rangeEnd;

    @Column(name = "price", nullable = true)
    private Double price;

    @Column(name = "currency", nullable = true, length = 20)
    private String currency;

    @Column(name = "note", nullable = true, length = -1)
    private String note;

    @Column(name = "is_active")
    private Byte isActive = 1;

    @Column(name = "is_promotion", nullable = true)
    private Boolean isPromotion;

    @Column(name = "new", nullable = true)
    private Boolean checkNew;

    @Transient
    private Integer leadTime;

    @Transient
    private String leadTimeNote;

    public MqqPriceEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MqqPriceEntity that = (MqqPriceEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(vendorCode, that.vendorCode) && Objects.equals(itemCode, that.itemCode) && Objects.equals(timeStart, that.timeStart) && Objects.equals(timeEnd, that.timeEnd) && Objects.equals(rangeStart, that.rangeStart) && Objects.equals(rangeEnd, that.rangeEnd) && Objects.equals(price, that.price) && Objects.equals(currency, that.currency) && Objects.equals(note, that.note) && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorCode, itemCode, timeStart, timeEnd, rangeStart, rangeEnd, price, currency, note, isActive);
    }

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

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Integer getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(Integer rangeStart) {
        this.rangeStart = rangeStart;
    }

    public Integer getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(Integer rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public Boolean getPromotion() {
        return isPromotion;
    }

    public void setPromotion(Boolean promotion) {
        isPromotion = promotion;
    }

    public Boolean getCheckNew() {
        return checkNew;
    }

    public void setCheckNew(Boolean checkNew) {
        this.checkNew = checkNew;
    }

    public Integer getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Integer leadTime) {
        this.leadTime = leadTime;
    }

    public String getLeadTimeNote() {
        return leadTimeNote;
    }

    public void setLeadTimeNote(String leadTimeNote) {
        this.leadTimeNote = leadTimeNote;
    }
}
