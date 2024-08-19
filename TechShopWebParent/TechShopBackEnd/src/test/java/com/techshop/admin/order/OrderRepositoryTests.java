package com.techshop.admin.order;

import com.techshop.common.entity.*;
import com.techshop.common.entity.order.*;
import com.techshop.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateOrderWithSingleProduct() {

        Customer customer = entityManager.find(Customer.class, 30L);
        Product product = entityManager.find(Product.class, 12L);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.setFirstName(customer.getFirstName());
        mainOrder.setLastName(customer.getLastName());
        mainOrder.setPhoneNumber(customer.getPhoneNumber());
        mainOrder.setAddressLine1(customer.getAddressLine1());
        mainOrder.setAddressLine2(customer.getAddressLine2());
        mainOrder.setCity(customer.getCity());
        mainOrder.setCountry(customer.getCountry().getName());
        mainOrder.setState(customer.getState());
        mainOrder.setPostalCode(customer.getPostalCode());

        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setProductCost(product.getCost());
        mainOrder.setShippingCost(10);
        mainOrder.setTax(0);
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliveryDate(new Date());
        mainOrder.setDeliveryDays(2);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(mainOrder);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(1);
        orderDetail.setUnitPrice(product.getPrice());
        orderDetail.setProductCost(product.getCost());
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setShippingCost(10);

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateOrderWithMultipleProducts() {

        Customer customer = entityManager.find(Customer.class, 31L);
        Product product1 = entityManager.find(Product.class, 12L);
        Product product2 = entityManager.find(Product.class, 13L);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.setFirstName(customer.getFirstName());
        mainOrder.setLastName(customer.getLastName());
        mainOrder.setPhoneNumber(customer.getPhoneNumber());
        mainOrder.setAddressLine1(customer.getAddressLine1());
        mainOrder.setAddressLine2(customer.getAddressLine2());
        mainOrder.setCity(customer.getCity());
        mainOrder.setCountry(customer.getCountry().getName());
        mainOrder.setState(customer.getState());
        mainOrder.setPostalCode(customer.getPostalCode());

        mainOrder.setSubtotal(product1.getPrice() + product2.getPrice());
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setShippingCost(10);
        mainOrder.setTax(0);
        mainOrder.setTotal(product1.getPrice() + product2.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliveryDate(new Date());
        mainOrder.setDeliveryDays(2);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProduct(product1);
        orderDetail1.setQuantity(1);
        orderDetail1.setUnitPrice(product1.getPrice());
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setShippingCost(10);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProduct(product2);
        orderDetail2.setQuantity(1);
        orderDetail2.setUnitPrice(product2.getPrice());
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setSubtotal(product2.getPrice());
        orderDetail2.setShippingCost(10);

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllOrders() {
        List<Order> listOrders = orderRepository.findAll();
        assertThat(listOrders.size()).isGreaterThan(0);
        listOrders.forEach(System.out::println);
    }

    @Test
    public void testUpdateOrder(){
        Order order = orderRepository.findById(2L).get();
        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        orderRepository.save(order);


        Order updatedOrder = entityManager.find(Order.class, 2L);

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    public void testGetOrderById(){
        Order order = orderRepository.findById(2L).get();
        System.out.println(order);
        assertThat(order).isNotNull();
    }

    @Test
    public void testDeleteOrder(){
        Long id = 2L;
        orderRepository.deleteById(id);
        Optional<Order> deletedOrder = orderRepository.findById(id);
        assertThat(deletedOrder).isNotPresent();
    }

    @Test
    public void testUpdateOrderTracks() {
        Order order = orderRepository.findById(4L).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks().size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateOrderTracks2() {
        Order order = orderRepository.findById(6L).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.PICKED);
        newTrack.setNotes(OrderStatus.PICKED.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PACKAGED);
        processingTrack.setNotes(OrderStatus.PACKAGED.defaultDescription());



        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);
        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks().size()).isGreaterThan(0);
    }

}
