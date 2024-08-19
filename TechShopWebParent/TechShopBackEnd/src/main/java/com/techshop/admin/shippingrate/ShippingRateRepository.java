package com.techshop.admin.shippingrate;

import com.techshop.admin.paging.SearchRepository;
import com.techshop.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Long> {
    ShippingRate findByCountryIdAndState(Long countryId, String state);

    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    @Modifying
    void updateCODSupport(Long id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE  %?1% or sr.state LIKE  %?1%")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    Long countById(Long id);
}