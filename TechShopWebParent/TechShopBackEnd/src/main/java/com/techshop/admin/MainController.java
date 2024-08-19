package com.techshop.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("")
    public String vewHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String vewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")){
//            return "redirect:/";
//        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";

        }
        return "redirect:/";

    }
}
