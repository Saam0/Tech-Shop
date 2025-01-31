package com.techshop.site;

import com.techshop.site.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping("")
    public String vewHomePage(Model model){
        model.addAttribute("listCategories", categoryService.listNoChildrenCategory());
        return "index";
    }

    @GetMapping("/login")
    public String vewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";

        }
        return "redirect:/";

    }
}
