package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VendorDTO implements Serializable {
    private String vendorCode;
    private String vendorName;
    private String otherName;
    private String taxCode;
    private String currency;
    private String phone;
    private String email;
    private String faxCode;
    private String address;
    private String contactId;
    private String contactName;
    private String contactPosition;
    private String contactTitle;
    private String contactGender;
    private String contactPhone;
    private String contactEmail;
    private String contactAddress;
    private String contactNationality;
    private LocalDate contactBirthDate;
    private Integer status;
    private final Map<String, String> vendorMap = new HashMap<>();


    public VendorDTO(String vendorCode, String vendorName, String otherName, String taxCode, String currency, String phone, String email, String faxCode, String address, String contactId, String contactName, String contactPosition, String contactTitle, String contactGender, String contactPhone, String contactEmail, String contactAddress, String contactNationality, LocalDate contactBirthDate, Integer status) {
        this.vendorCode = vendorCode;
        this.vendorName = vendorName;
        this.otherName = otherName;
        this.taxCode = taxCode;
        this.currency = currency;
        this.phone = phone;
        this.email = email;
        this.faxCode = faxCode;
        this.address = address;
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactPosition = contactPosition;
        this.contactTitle = contactTitle;
        this.contactGender = contactGender;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.contactAddress = contactAddress;
        this.contactNationality = contactNationality;
        this.contactBirthDate = contactBirthDate;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public VendorDTO() {
    }
    @JsonAnyGetter
    public Map<String, String> getVendorMap() {
        return vendorMap;
    }
    @JsonAnySetter
    public void setVendorMap(String key, String value) {
        vendorMap.put(key, value);
    }
}
