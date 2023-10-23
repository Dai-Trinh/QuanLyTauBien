package com.facenet.mdm.domain;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "customer")
public class CustomerEntity extends AbstractAuditingEntity<Integer> {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "customer_id")
    private Integer id;

    @Basic
    @Column(name = "customer_code")
    private String customerCode;

    @Basic
    @Column(name = "customer_name")
    private String customerName;

    @Basic
    @Column(name = "customer_phone")
    private String customerPhone;

    @Basic
    @Column(name = "customer_email")
    private String customerEmail;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "customer_type")
    private String customerType;

    @Basic
    @Column(name = "status")
    private Integer status;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive = true;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
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

    public CustomerEntity() {}

    public CustomerEntity(CustomerEntity that) {
        this.id = that.id;
        this.customerCode = that.customerCode;
        this.customerName = that.customerName;
        this.customerPhone = that.customerPhone;
        this.customerEmail = that.customerEmail;
        this.address = that.address;
        this.status = that.status;
        this.customerType = that.customerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerEntity that = (CustomerEntity) o;

        if (id != that.id) return false;
        if (customerCode != null ? !customerCode.equals(that.customerCode) : that.customerCode != null) return false;
        if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;
        if (customerPhone != null ? !customerPhone.equals(that.customerPhone) : that.customerPhone != null) return false;
        if (customerEmail != null ? !customerEmail.equals(that.customerEmail) : that.customerEmail != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (isActive != null ? !isActive.equals(that.isActive) : that.isActive != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (customerCode != null ? customerCode.hashCode() : 0);
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (customerPhone != null ? customerPhone.hashCode() : 0);
        result = 31 * result + (customerEmail != null ? customerEmail.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0);
        return result;
    }
}
