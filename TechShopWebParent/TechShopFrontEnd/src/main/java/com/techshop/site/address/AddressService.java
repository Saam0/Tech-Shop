package com.techshop.site.address;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    public Address get(Long addressId, Long customerId) {
        return addressRepository.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Long addressId, Long customerId) {
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Long addressId, Long customerID) {
        if (addressId>0) {
            addressRepository.setDefaultAddress(addressId);
        }
        addressRepository.setNonDefaultForOthers(addressId, customerID);
    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findDefaultByCustomer(customer.getId());
    }
}
