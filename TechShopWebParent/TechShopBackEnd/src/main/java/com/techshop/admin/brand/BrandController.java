package com.techshop.admin.brand;

import com.techshop.admin.AmazonS3Util;
import com.techshop.admin.FileUploadUtil;
import com.techshop.admin.category.CategoryCsvExporter;
import com.techshop.admin.category.CategoryService;
import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.admin.user.UserService;
import com.techshop.common.entity.Brand;
import com.techshop.common.entity.Category;
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
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/brands")
//    public String listAllBrands(Model model){
//        model.addAttribute("listBrands",brandService.listAllBrands());
//        return "brands/brands";
//    }

    @GetMapping("/brands")
    public String listFirstPage(){
        return "redirect:/brands/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listAllBrandsByPage(@PagingAndSortingParam(moduleURL = "/brands", listName = "listBrands") PagingAndSortingHelper helper,
                                      @PathVariable(name = "pageNum") int pageNum,
                                      Model model) {

         brandService.findAllByPage(pageNum, helper);
        return "brands/brands";
    }



    @GetMapping("/brands/new")
    public String newBrand(Model model){

        List<Category> listCategories = categoryService.getAllCategoriesUsedInForm();
        model.addAttribute("listCategories",listCategories);
        model.addAttribute("brand",new Brand());
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage")MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes, Model model) throws IOException {
        if (!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-logos/" + savedBrand.getId();

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

//            String uploadDir = "TechShopWebParent/brand-logos/" + savedBrand.getId();
//
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);

        }else {
            brandService.save(brand);
        }
        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");

        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public  String editBrand(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes){
        try {
            Brand brand = brandService.getById(id);
            List<Category> listCategories = categoryService.getAllCategoriesUsedInForm();

            model.addAttribute("brand",brand);
            model.addAttribute("listCategories",listCategories);
            model.addAttribute("pageTitle", "Edit brand (ID: " + id + ")");
            return "brands/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id, Model model,
                              RedirectAttributes redirectAttributes){
        try {
            brandService.delete(id);
            String brandDir = "brand-logos/" + id;
            AmazonS3Util.removeFolder(brandDir);

//            String brandDir = "TechShopWebParent/brand-logos/" + id;
//            FileUploadUtil.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message","The brand ID " + id +
                    "has been deleted successfully");

        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

        }
        return "redirect:/brands";
    }

    @GetMapping("/brands/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Brand> brands = brandService.listAllBrands();
        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(brands,response);
    }
}
