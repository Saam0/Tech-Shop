package com.techshop.common.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAddressWithCountry extends AbstractAddress{
    @ManyToOne
    @JoinColumn(name = "country_id")
    protected Country country;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        String address = firstName;

        if(lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if(addressLine1 != null && !addressLine1.isEmpty()) address += ", " + addressLine1;
        if(addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if(city != null && !city.isEmpty()) address += ", " + city;
        if(state != null && !state.isEmpty()) address += ", " + state;
        if(postalCode != null && !postalCode.isEmpty()) address += ", Postal code: " + postalCode;

        address += ", " + country.getName();

        if(phoneNumber != null && !phoneNumber.isEmpty()) address += ", Phone Number: " + phoneNumber;
        return address;
    }
}
