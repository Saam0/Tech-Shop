package com.techshop.site.customer;

import com.techshop.common.entity.Customer;
import com.techshop.common.exception.CustomerNotFoundException;
import com.techshop.site.Utility;
import com.techshop.site.setting.EmailSettingBag;
import com.techshop.site.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);
            model.addAttribute("message", "We have sent a reset password link to your email." +
                    "Please check your email.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Could not send email");
        }
        return "customer/forgot_password_form";
    }

    public void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSender mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,</p>" +
                "<p>You have requested to reset your password.</p>" +
                "Click thr link below to change your password:</p>" +
                "<p><a href=\"" + link + "\"> Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your password, " +
                "or you have not made the request.</p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content,true);
        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetForm(@Param("token")String token,Model model){
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer!=null){
            model.addAttribute("token", token);
        }else {
            model.addAttribute("pageTitle","Invalid token");
            model.addAttribute("message", "Invalid token");
            return "message";
        }

        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       Model model){

        try {
            customerService.updatePassword(token, newPassword);

            model.addAttribute("pageTitle","Reset your password");
            model.addAttribute("title", "Reset your password");
            model.addAttribute("message", "You have successfully changed your password");

            return "message";
        } catch (CustomerNotFoundException e) {

            model.addAttribute("message", e.getMessage());
            model.addAttribute("pageTitle","Invalid token");

            return "message";
        }
    }
}
