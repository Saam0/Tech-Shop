package com.techshop.site.security;

import com.techshop.common.entity.Customer;
import com.techshop.site.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = this.customerRepository.findByEmail(email);
        if (customer!=null){
            return new CustomerUserDetails(customer);
        }
        throw new UsernameNotFoundException("Could not find customer with email: " + email);
    }

}
