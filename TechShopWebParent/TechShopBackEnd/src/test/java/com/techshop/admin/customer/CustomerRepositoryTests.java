package com.techshop.admin.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {

    @Autowired private CustomerRepository customerRepository;

    @Test
    public void testListAllCustomersByKeyword() {
        String keyword = "Sacramento";
        customerRepository.findAll(keyword, PageRequest.of(0, 10, Sort.by("firstName").ascending())).forEach(System.out::println);
    }

    @Test
    public void testListAllCustomers() {
        customerRepository.findAll(PageRequest.of(0, 10, Sort.by("firstName").ascending())).forEach(System.out::println);
    }

}
