package com.techshop.site.shipping;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {
    ShippingRate findByCountryAndState(Country country, String state);

}