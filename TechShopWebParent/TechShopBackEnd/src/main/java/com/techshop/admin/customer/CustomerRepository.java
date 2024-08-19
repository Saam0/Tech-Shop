package com.techshop.admin.customer;

import com.techshop.admin.paging.SearchRepository;
import com.techshop.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends SearchRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
     Customer getCustomerByEmail(String email);

    @Modifying
    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
     void updateEnabledStatus(Long id, boolean enabled);

    @Query("SELECT c FROM Customer c WHERE concat(c.email,' ',c.firstName,' ',c.lastName,' '," +
            "c.addressLine1, ' ', c.city, ' ', c.state, ' ', c.postalCode, ' '," +
            "c.postalCode,' ',c.country.name) LIKE %?1%")
   Page<Customer> findAll(String keyword, Pageable pageable);


    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
     Customer findByEmail(String email);

    Long countById(Long id);






}