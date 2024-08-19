package com.techshop.site.shoppingcart;

import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(Customer customer);

    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    @Query("UPDATE CartItem c set c.quantity =?1 where c.customer.id =?2 and c.product.id =?3")
    void updateQuantity(int quantity, Long customerId, Long productId);

    void deleteCartItemByCustomer_IdAndProduct_Id(Long customerId, Long productId);

    void deleteCartItemByCustomer_Id(Long customerId);
}