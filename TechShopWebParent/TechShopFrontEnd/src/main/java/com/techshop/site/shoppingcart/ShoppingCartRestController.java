package com.techshop.site.shoppingcart;

import com.techshop.common.entity.Customer;
import com.techshop.common.exception.CustomerNotFoundException;
import com.techshop.site.Utility;
import com.techshop.site.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Long productId,
                                   @PathVariable("quantity") Integer quantity,
                                   HttpServletRequest request){
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);

            return  updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to add this product to cart";
        } catch (ShoppingCartException ex) {
            return ex.getMessage();
        }
    }


    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticationToken(request);
        if (email==null){
            throw new CustomerNotFoundException("No authentication customer");
        }
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable("productId") Long productId,
                                   @PathVariable("quantity") Integer quantity,
                                   HttpServletRequest request){
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = shoppingCartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of product.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String updateQuantity(@PathVariable("productId") Long productId,
                                 HttpServletRequest request){
        try {
            Customer customer = getAuthenticatedCustomer(request);
            shoppingCartService.removeProduct(productId, customer);

            return "The product has been removed from your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to remove product.";
        }
    }
}
