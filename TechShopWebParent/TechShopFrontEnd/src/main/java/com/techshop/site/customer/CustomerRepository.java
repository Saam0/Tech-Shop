package com.techshop.site.customer;

import com.techshop.common.entity.AuthenticationType;
import com.techshop.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);

    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c  SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1 ")
    @Modifying
    void enableCustomer(Long id);

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Long customerId, AuthenticationType authenticationType);

    Customer findByResetPasswordToken(String token);
}