package com.techshop.site.customer;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.Customer;
import com.techshop.common.exception.CustomerNotFoundException;
import com.techshop.site.Utility;
import com.techshop.site.security.CustomerUserDetails;
import com.techshop.site.security.oauth.CustomerOAuth2User;
import com.techshop.site.setting.EmailSettingBag;
import com.techshop.site.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private  CustomerService customerService;

    @Autowired
    private  SettingService settingService;



    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listCountries = customerService.listAllCountries();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());
        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String registerCustomer(Customer customer, Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(customer, request);
        model.addAttribute("pageTitle", "Registration Successful");
        return "register/register_success";
    }

    private void sendVerificationEmail(Customer customer, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");


        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());
        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.err.println("to address: " + toAddress);
        System.err.println("verify url: " + verifyURL);

    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_file");
    }

    @GetMapping("/account_details")
    public String viewAccountDetail(Model model, HttpServletRequest request){
        String email = Utility.getEmailOfAuthenticationToken(request);
        Customer customer = customerService.getCustomerByEmail(email);
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("customer" , customer);
        model.addAttribute("listCountries" , listCountries);

        updateNameForAuthenticatedCustomer(customer,request);
        return "customer/account_form";
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomerUserDetails authenticatedCustomer) {
            authenticatedCustomer.getCustomer().setFirstName(customer.getFirstName());
            authenticatedCustomer.getCustomer().setLastName(customer.getLastName());
        }
        if (authentication.getPrincipal() instanceof CustomerOAuth2User customerOAuth2User) {
            customerOAuth2User.setFullName(customer.getFullName());
        }
    }



    @PostMapping("/update_account_details")
    public String updateAccountDetails(Customer customer,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) throws CustomerNotFoundException {
        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");

        updateNameForAuthenticatedCustomer(customer,request);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/account_details";

        if ("address_book".equals(redirectOption)){
            redirectURL = "redirect:/address_book";
        } else if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";

        }else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/address_book?redirect=checkout";

        }
        return redirectURL;

    }

//    private String getEmailOfAuthenticationToken(HttpServletRequest request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return null;
//        }
//        if (authentication.getPrincipal() instanceof UserDetails) {
//            return ((UserDetails) authentication.getPrincipal()).getUsername();
//        }
//        if (authentication.getPrincipal() instanceof OAuth2User) {
//            return ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
//        }
//        return null;
//    }


}
