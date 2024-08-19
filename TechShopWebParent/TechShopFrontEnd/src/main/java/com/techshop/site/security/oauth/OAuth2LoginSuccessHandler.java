package com.techshop.site.security.oauth;

import com.techshop.common.entity.AuthenticationType;
import com.techshop.common.entity.Customer;
import com.techshop.site.customer.CustomerRepository;
import com.techshop.site.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private  CustomerService customerService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        String name = oAuth2User.getName();
        String countryCode = request.getLocale().getCountry();
        String clientName = oAuth2User.getClientName();

//        System.err.println("Client name: " + clientName);

        Customer customer = customerService.getCustomerByEmail(email);
        AuthenticationType authenticationType = getAuthenticationType(clientName);

        if (customer == null) {
            customerService.addNewCustomerUponOAuth2Login(name, email, countryCode, authenticationType);
        } else {
            customerService.updateAuthenticationType(customer, authenticationType);
            oAuth2User.setFullName(customer.getFullName());
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if (clientName.equals("Google")) {
            return AuthenticationType.GOOGLE;
        } else if (clientName.equals("Facebook")) {
            return AuthenticationType.FACEBOOK;
        } else {
            return AuthenticationType.DATABASE;
        }
    }
}
