package com.techshop.site.customer;

import com.techshop.common.entity.AuthenticationType;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCustomer1() {
        Long id = 12L;
        Country country = entityManager.find(Country.class, id);
        Customer customer = new Customer();
        customer.setFirstName("Samvel");
        customer.setLastName("Yazhyan");
        customer.setEmail("sam.yan0081@gmail.com");
        customer.setCountry(country);
        customer.setPassword("123456");
        customer.setPhoneNumber("055-55-55-55");
        customer.setCity("Echmiadzin");
        customer.setState("Armavir");
        customer.setAddressLine1("18/1 Hayots Banak street");
        customer.setPostalCode("0011");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);


    }

//    make  testCreateCustomer3 with country Russia, city Moscow, state Moscow, address 18/1 Lenin street, postal code 0022.

    @Test
    public void testCreateCustomer2() {
        Long id = 13L;
        Country country = entityManager.find(Country.class, id);
        Customer customer = new Customer();
        customer.setFirstName("Hakob");
        customer.setLastName("Hakobyan");
        customer.setEmail("SamvelYazhyan@gmail.com");
        customer.setCountry(country);
        customer.setPassword("123456");
        customer.setPhoneNumber("055-55-55-44");
        customer.setCity("Moscow");
        customer.setState("Moscow");
        customer.setAddressLine1("18/1 Lenin street");
        customer.setPostalCode("0022");
        customer.setVerificationCode("code-123");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);

    }

    @Test
    public void testListAllCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out::println);
        assertThat(customers).isNotNull();
    }

    @Test
    public void testUpdateCustomer() {
        Long id = 1L;
        String lastName = "Yan";
        Customer customer = customerRepository.findById(id).get();
        customer.setFirstName("Samvel");
        customer.setLastName(lastName);
        customer.setEnabled(true);

        Customer savedCustomer = customerRepository.save(customer);
        assertThat(savedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testGetCustomer() {
        Long id = 1L;
        Customer customer = customerRepository.findById(id).get();
        assertThat(customer).isNotNull();
    }

    @Test
    public void testDeleteCustomer() {
        Long id = 2L;
        customerRepository.deleteById(id);

        Optional<Customer> deletedCustomer = customerRepository.findById(id);
        assertThat(deletedCustomer).isEmpty();
    }


    @Test
    public void testFindByEmail() {
        String email = "sam.yan0081@gmail.com";
        Customer customer = customerRepository.findByEmail(email);

        assertThat(customer.getEmail()).isEqualTo(email);
        System.out.println(customer);
    }

    @Test
    public void findByVerificationCode(){
        String code = "code-123";
        Customer customer = customerRepository.findByVerificationCode(code);
        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public  void testUpdateAuthenticationType(){
        Long id = 1L;
        customerRepository.updateAuthenticationType(id, AuthenticationType.FACEBOOK);
        Customer customer = customerRepository.findById(id).get();
        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.FACEBOOK);
    }


}
