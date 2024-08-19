package com.techshop.site.address;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTests {
    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testAddNew() {
        Long customerId = 30L;
        Long countryId = 12L; // USA

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("Nina");
        newAddress.setLastName("Yazhyan");
        newAddress.setPhoneNumber("044448899");
        newAddress.setAddressLine1("14 Komitas Street");
        newAddress.setAddressLine2("30 Building");
        newAddress.setCity("Artashat");
        newAddress.setState("Ararat");
        newAddress.setPostalCode("0011");

        Address savedAddress = addressRepository.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Long customerId = 30L;
        List<Address> listAddresses = addressRepository.findByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);
        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Long addressId = 1L;
        Long customerId = 30L;
        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate(){
        Long addressId = 1L;
        String phoneNumber = "0987654321";

        Address address = addressRepository.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = addressRepository.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDelete(){
        Long addressId = 1L;
        Long customerId = 30L;
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
    	Long defaultAddressId = 2L;
    	addressRepository.setDefaultAddress(defaultAddressId);

        Address address = addressRepository.findById(defaultAddressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefault() {
    	Long addressId = 2L;
        Long customerId = 30L;
    	addressRepository.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    public void testFindDefaultByCustomer() {
    	Long customerId = 30L;
    	Address address = addressRepository.findDefaultByCustomer(customerId);
    	assertThat(address).isNotNull();
    	System.out.println(address);
    }
}
