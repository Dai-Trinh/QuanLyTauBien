package com.facenet.mdm.domain;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "vendor")
public class VendorEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id", nullable = false)
    private Integer id;

    @Column(name = "vendor_code")
    private String vendorCode;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "other_name")
    private String otherName;

    @Column(name = "tax_code")
    private String taxCode;

    @Column(name = "currency")
    private String currency;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "fax_code")
    private String faxCode;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_id")
    private String contactId;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_position")
    private String contactPosition;

    @Column(name = "contact_title")
    private String contactTitle;

    @Column(name = "contact_gender")
    private String contactGender;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_address")
    private String contactAddress;

    @Column(name = "contact_nationality")
    private String contactNationality;

    @Column(name = "contact_birth_date")
    private LocalDate contactBirthDate;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaxCode() {
        return faxCode;
    }

    public void setFaxCode(String faxCode) {
        this.faxCode = faxCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPosition() {
        return contactPosition;
    }

    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getContactGender() {
        return contactGender;
    }

    public void setContactGender(String contactGender) {
        this.contactGender = contactGender;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactNationality() {
        return contactNationality;
    }

    public void setContactNationality(String contactNationality) {
        this.contactNationality = contactNationality;
    }

    public LocalDate getContactBirthDate() {
        return contactBirthDate;
    }

    public void setContactBirthDate(LocalDate contactBirthDate) {
        this.contactBirthDate = contactBirthDate;
    }

    public VendorEntity(VendorEntity that) {
        this.id = that.id;
        this.vendorCode = that.vendorCode;
        this.vendorName = that.vendorName;
        this.otherName = that.otherName;
        this.taxCode = that.taxCode;
        this.currency = that.currency;
        this.phone = that.phone;
        this.email = that.email;
        this.faxCode = that.faxCode;
        this.address = that.address;
        this.contactId = that.contactId;
        this.contactName = that.contactName;
        this.contactPosition = that.contactPosition;
        this.contactTitle = that.contactTitle;
        this.contactGender = that.contactGender;
        this.contactPhone = that.contactPhone;
        this.contactEmail = that.contactEmail;
        this.contactAddress = that.contactAddress;
        this.contactBirthDate = that.contactBirthDate;
        this.status = that.status;
    }

    public VendorEntity() {}
}
