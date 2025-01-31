package com.techshop.admin.order;

import com.techshop.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o from Order o where  o.firstName LIKE %?1%"
            + " OR o.lastName LIKE %?1%"
            + " OR o.phoneNumber LIKE %?1%"
            + " OR o.addressLine1 LIKE %?1%"
            + " OR o.addressLine2 LIKE %?1%"
            + " OR o.city LIKE %?1%"
            + " OR o.state LIKE %?1%"
            + " OR o.country LIKE %?1%"
            + " OR o.postalCode LIKE %?1%"
            + " OR o.paymentMethod LIKE %?1%"
            + " OR o.status LIKE %?1%"
            + " OR o.customer.email LIKE %?1%"
            + " OR o.customer.firstName LIKE %?1%"
            + " OR o.customer.lastName LIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);

    Long countById(Long id);
}