package com.techshop.admin.category;

import com.techshop.admin.AmazonS3Util;
import com.techshop.admin.FileUploadUtil;
import com.techshop.admin.user.UserNotFoundException;
import com.techshop.admin.user.UserService;
import com.techshop.admin.user.export.UserCsvExporter;
import com.techshop.common.entity.Category;
import com.techshop.common.entity.User;
import com.techshop.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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

import static com.techshop.admin.category.CategoryService.ROOT_CATEGORIES_PER_PAGE;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listCategoriesFirstPage( Model model) {

        return listCategoriesByPage(1, "desc", null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listCategoriesByPage(@PathVariable("pageNum") int pageNum,
                                       @Param("sortDir") String sortDir,
//                                       @Param("sortField") String sortField,
                                       @Param("keyword") String keyword,Model model) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categoryList = categoryService.getAllHierarchicalCategories(pageInfo,pageNum,sortDir,keyword);

        long startCount = (long) (pageNum - 1) * ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPage",pageInfo.getTotalPages());
        model.addAttribute("totalItems",pageInfo.getTotalElements());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("sortField","name");
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("keyword",keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listCategories", categoryList);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("moduleURL", "/categories");
        return "categories/categories";
    }


    @GetMapping("/categories/new")
    public String newCategories(Model model) {
        model.addAttribute("pageTitle", "Create new category");
        model.addAttribute("listCategories", categoryService.getAllCategoriesUsedInForm());
        model.addAttribute("category", new Category());

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes,
                               Model model) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

//            String uploadDir = "TechShopWebParent/category-images/" + savedCategory.getId();
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }
        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Long id,
                               Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getById(id);
            model.addAttribute("pageTitle", "Edit category (ID: " + id + ")");
            model.addAttribute("listCategories", categoryService.getAllCategoriesUsedInForm());
            model.addAttribute("category", category);
            return "categories/category_form";

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Long id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The category Id:" + id + " has been " + status);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            String categoryDir = "category-images/" + id;
            AmazonS3Util.removeFolder(categoryDir);

//            String categoryDir = "TechShopWebParent/category-images/" + id;
//            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category ID " +
                    id + " has been deleted successfully");

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            System.err.println(e.getMessage());
        }

        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> allCategories = categoryService.getAllCategoriesUsedInForm();
        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(allCategories,response);
    }
}
