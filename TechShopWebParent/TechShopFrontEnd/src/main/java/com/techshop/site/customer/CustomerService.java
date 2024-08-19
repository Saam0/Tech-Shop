package com.techshop.site.customer;

import com.techshop.common.entity.AuthenticationType;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import com.techshop.common.exception.CustomerNotFoundException;
import com.techshop.site.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private  CustomerRepository customerRepository;
    @Autowired
    private  CountryRepository countryRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;


    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new java.util.Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
        customerRepository.save(customer);
    }


    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enableCustomer(customer.getId());
            return true;
        }
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (customer.getAuthenticationType() != authenticationType) {
            customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
        }
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void addNewCustomerUponOAuth2Login(String name, String email, String countryCode, AuthenticationType authenticationType) {

        Customer customer = new Customer();
        customer.setEmail(email);
        setName(customer, name);
        customer.setEnabled(true);
        customer.setCreatedTime(new java.util.Date());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setAddressLine2("");
        customer.setCity("");
        customer.setState("");
        customer.setPostalCode("");
        customer.setPhoneNumber("");
//        System.err.println("countryCode: " + countryCode);
        customer.setCountry(countryRepository.findByCode(countryCode));
        customerRepository.save(customer);
    }

    private void setName(Customer customer, String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length == 1) {
            customer.setFirstName(nameParts[0]);
            customer.setLastName("");
        } else {
            customer.setFirstName(nameParts[0]);
            customer.setLastName(nameParts[1]);
        }
    }

    public Customer getByID(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Could not find any customer with ID " + id));
    }
    public void update(Customer customerInForm) throws CustomerNotFoundException {
        Customer customerInDB = getByID(customerInForm.getId());

        if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if (!customerInForm.getPassword().isEmpty()){
                customerInForm.setPassword(passwordEncoder.encode(customerInForm.getPassword()));
            }else {
                customerInForm.setPassword(customerInDB.getPassword());
            }
        }else {
            customerInForm.setPassword(customerInDB.getPassword());
        }
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
        customerRepository.save(customerInForm);
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = getCustomerByEmail(email);
        if (customer!=null){
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
            return token;
        }else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public Customer getByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = getByResetPasswordToken(token);
        if (customer==null){
            throw new CustomerNotFoundException("Np customer found: Invalid token");
        }
        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        encodePassword(customer);
        customerRepository.save(customer);
    }
}
