package com.techshop.site.shoppingcart;

import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepositoryTests {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveItem() {
        Long customerId = 30L;
        Long productId = 1L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        cartItemRepository.save(cartItem);

        assertThat(cartItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSave2Item() {
        Long customerId = 31L;
        Long productId = 3L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCustomer(customer);
        cartItem2.setProduct(new Product(7L));
        cartItem2.setQuantity(2);


        List<CartItem> cartItems = cartItemRepository.saveAll(List.of(cartItem, cartItem2));

        assertThat(cartItems.size()).isGreaterThan(0);
    }


    @Test
    public void testFindByCustomer() {
        Long customerId = 31L;
        List<CartItem> listItems = cartItemRepository.findByCustomer(new Customer(customerId));
        listItems.forEach(System.out::println);
        assertThat(listItems.size()).isEqualTo(2);

    }


    @Test
    public void testFindByCustomerAndProduct() {
        Long customerId = 31L;
        Long productId = 3L;

        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNotNull();
        System.out.println(item);
    }

    @Test
    public void testUpdateQuantity() {
        Long customerId = 31L;
        Long productId = 3L;
        int quantity = 4;

        cartItemRepository.updateQuantity(quantity, customerId, productId);
        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item.getQuantity()).isEqualTo(4);
        System.out.println(item);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Long customerId = 31L;
        Long productId = 7L;
        cartItemRepository.deleteCartItemByCustomer_IdAndProduct_Id(customerId,productId);
        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
        assertThat(item).isNull();
    }
}