package com.techshop.admin.user.controllers;

import com.techshop.admin.FileUploadUtil;
import com.techshop.admin.security.TechShopUserDetails;
import com.techshop.admin.user.UserNotFoundException;
import com.techshop.admin.user.UserService;
import com.techshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal TechShopUserDetails loggedUser,
                              Model model) {
        User user = loggedUser.getUser();
        model.addAttribute("user", user);
        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String saveUserDetails(User user, RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal TechShopUserDetails loggedUser,
                                  @RequestParam("image") MultipartFile multipartFile) throws IOException, UserNotFoundException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
            User saveUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + saveUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.updateAccount(user);
        }

        loggedUser.getUser().setFirstName(user.getFirstName());
        loggedUser.getUser().setLastName(user.getLastName());
        loggedUser.getUser().setPhotos(user.getPhotos());

        redirectAttributes.addFlashAttribute("message", "Your account details have been updates");
        return "redirect:/account";
    }
}
