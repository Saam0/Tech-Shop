package com.techshop.site.shoppingcart;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.ShippingRate;
import com.techshop.common.exception.CustomerNotFoundException;
import com.techshop.site.Utility;
import com.techshop.site.address.AddressService;
import com.techshop.site.customer.CustomerService;
import com.techshop.site.shipping.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private AddressService addressService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request){
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        float estimatedTotal = 0.0f;
        for (CartItem cartItem : cartItems) {
            estimatedTotal += cartItem.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null) {
             shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }


        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("estimatedTotal", estimatedTotal);
        model.addAttribute("cartItems", cartItems);
        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request)  {
        String email = Utility.getEmailOfAuthenticationToken(request);

        return customerService.getCustomerByEmail(email);
    }
}
