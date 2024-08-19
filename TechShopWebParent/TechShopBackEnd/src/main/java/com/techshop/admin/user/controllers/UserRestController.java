package com.techshop.admin.user.controllers;

import com.techshop.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;

    @PostMapping("/Users/check_email")
    public String checkDuplicateEmail(@RequestParam(name = "id",required = false) Long id, @RequestParam("email") String email) {
        return userService.isEmailUnique(id,email) ? "OK" : "Duplicated";
    }
}
