package com.techshop.admin.user;


import com.techshop.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateFirstRole(){
        Role roleAdmin= new Role("Admin", "manage everything");
        Role savedRole = repo.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }
    @Test
    public void testCreateRestRole(){
        Role SalesPerson = new Role("Salesperson","manage product price, customers, shipping, orders and sales report");
        Role Editor = new Role("Editor","manage categories, brands, products, articles and menus");
        Role Shipper = new Role("Shipper","view products, view orders and update order status");
        Role Assistant = new Role("Assistant","manage questions and reviews");
        repo.saveAll(List.of(SalesPerson,Editor,Shipper,Assistant));

    }
}