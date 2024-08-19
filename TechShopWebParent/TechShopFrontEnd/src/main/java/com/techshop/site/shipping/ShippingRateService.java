package com.techshop.site.shipping;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {

    @Autowired ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        Country country = customer.getCountry();
        String state = null;
        if (customer.getState() == null || customer.getState().isEmpty()) {
            state = customer.getCity();
        }else {
            state = customer.getState();
        }

        return shippingRateRepository.findByCountryAndState(country, state);
    }

    public ShippingRate getShippingRateForAddress(Address  address) {
        Country country = address.getCountry();
        String state = null;
        if (address.getState() == null || address.getState().isEmpty()) {
            state = address.getCity();
        }else {
            state = address.getState();
        }

        return shippingRateRepository.findByCountryAndState(country, state);
    }
}
