package com.techshop.site.address;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import com.techshop.site.Utility;
import com.techshop.site.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/address_book")
    public String listAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("customer", customer);
        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationToken(request);
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");
        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.saveAddress(address);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("checkout".equals(redirectOption)){
            redirectURL += "?redirect=checkout";
        } else if ("cart".equals(redirectOption)) {
            redirectURL += "?redirect=cart";

        }
        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");
        return redirectURL;
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Long addressId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);
        Address address = addressService.get(addressId, customer.getId());
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");
        return "address_book/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Long addressId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.delete(addressId,customer.getId());

        redirectAttributes.addFlashAttribute("message", "The address has been deleted.");
        return "redirect:/address_book";
    }

    @GetMapping("/address_book/set_default/{id}")
    public String setDefaultAddress(@PathVariable("id") Long addressId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(addressId, customer.getId());

        redirectAttributes.addFlashAttribute("message", "The default address has been updated.");

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("cart".equals(redirectOption)){
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/checkout";

        }
        System.err.println("redirectURL: " + redirectURL);
        return redirectURL;
    }
}
