package com.techshop.admin.customer;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.setting.country.CountryRepository;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import com.techshop.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class CustomerService {
    public static final int CUSTOMER_PER_PAGE = 10;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMER_PER_PAGE, customerRepository);
    }

    public void updateCustomerEnabledStatus(Long id, boolean enabled) {
        customerRepository.updateEnabledStatus(id, enabled);
    }

    public Customer getByID(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Could not find any customer with ID " + id));
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(Long id, String email) {
        Customer customerByEmail = customerRepository.getCustomerByEmail(email);
        if (customerByEmail == null) return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (customerByEmail != null) return false;
        } else {
            if (customerByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    public void save(Customer customerInForm) throws CustomerNotFoundException {
        Customer customerInDB = getByID(customerInForm.getId());
        if (!customerInForm.getPassword().isEmpty()){
           customerInForm.setPassword(passwordEncoder.encode(customerInForm.getPassword()));
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

    public void delete(Long id) throws CustomerNotFoundException {
        Customer customer = getByID(id);
        customerRepository.delete(customer);
    }

}
