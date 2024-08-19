package com.techshop.common.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAddress extends IdBasedEntity{
    @Column(length = 45, nullable = false)
    protected String firstName;
    @Column(length = 45, nullable = false)
    protected String lastName;
    @Column(nullable = false, length = 15)
    protected String phoneNumber;
    @Column(nullable = false, length = 64)
    protected String addressLine1;
    @Column(length = 64)
    protected String addressLine2;
    @Column(nullable = false, length = 45)
    protected String city;
    @Column(nullable = false, length = 45)
    protected String state;
    @Column(nullable = false, length = 10)
    protected String postalCode;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
