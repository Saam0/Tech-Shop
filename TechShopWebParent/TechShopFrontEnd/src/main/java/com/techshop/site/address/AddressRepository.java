package com.techshop.site.address;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(Customer customer);

    @Query("select a from Address a where a.id = ?1 and a.customer.id = ?2")
    Address findByIdAndCustomer(Long addressId, Long customerId);

    @Modifying
    @Query("delete from Address a where a.id = ?1 and a.customer.id = ?2")
    void deleteByIdAndCustomer(Long addressId, Long customerId);

    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
    @Modifying
    void setDefaultAddress(Long addressId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping=false WHERE a.id != ?1 AND a.customer.id = ?2")
    void setNonDefaultForOthers(Long defaultId, Long customerId);

    @Query("select a from Address a where a.defaultForShipping = true and a.customer.id = ?1")
    Address findDefaultByCustomer(Long customerId);

}