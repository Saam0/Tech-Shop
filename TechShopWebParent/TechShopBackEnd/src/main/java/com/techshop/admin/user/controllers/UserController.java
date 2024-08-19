package com.techshop.admin.user.controllers;

import com.techshop.admin.AmazonS3Util;
import com.techshop.admin.FileUploadUtil;
import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.admin.user.UserNotFoundException;
import com.techshop.admin.user.UserService;
import com.techshop.admin.user.export.UserCsvExporter;
import com.techshop.admin.user.export.UserExcelExporter;
import com.techshop.admin.user.export.UserPdfExporter;
import com.techshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/users")
    public String listUsersFirstPage() {
        return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/users/page/{pageNum}")
    public String listAllUsersByPage(@PagingAndSortingParam(moduleURL = "/users", listName = "listAllUsers") PagingAndSortingHelper helper,
                                     @PathVariable(name = "pageNum") int pageNum,
                                     Model model) {

        userService.getAllUsersByPage(pageNum, helper);

        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("listAllRoles", userService.getAllRoles());
        model.addAttribute("pageTitle", "Create new user");

        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        System.err.println("user: " + user.getId());
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            user.setPhotos(fileName);
            User saveUser = userService.save(user);

            String uploadDir = "user-photos/" + saveUser.getId();
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user) {
        String firstPartEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id,
                           Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit user (ID:" + id + ")");
            model.addAttribute("listAllRoles", userService.getAllRoles());
            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes) {

        try {
            userService.deleteById(id);

            String uploadDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(uploadDir);

            redirectAttributes.addFlashAttribute("message", "The user Id " + id + " has been" +
                    " deleted successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Long id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The user Id:" + id + " has been " + status);
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<User> allUsers = userService.getAllUsers();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(allUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> allUsers = userService.getAllUsers();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(allUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> allUsers = userService.getAllUsers();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(allUsers, response);
    }
}
