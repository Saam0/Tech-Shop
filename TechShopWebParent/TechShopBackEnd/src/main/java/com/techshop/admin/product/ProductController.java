package com.techshop.admin.product;

import com.techshop.admin.FileUploadUtil;
import com.techshop.admin.brand.BrandService;
import com.techshop.admin.category.CategoryService;
import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.admin.security.TechShopUserDetails;
import com.techshop.admin.user.RoleRepository;
import com.techshop.common.entity.*;
import com.techshop.common.entity.product.Product;
import com.techshop.common.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

import static com.techshop.admin.product.ProductSaveHelper.*;

@Controller
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("products")
    public String listFirstPage(Model model) {
        return "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

    }

    @GetMapping("/products/page/{pageNum}")
    public String listAllProductsByPage(@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
                                        @PathVariable(name = "pageNum") int pageNum,
                                        @Param("categoryId") Long categoryId,
                                        Model model) {

        productService.findAllByPage(pageNum, helper, categoryId);

        List<Category> listCategories = categoryService.getAllCategoriesUsedInForm();

        if (categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }
        model.addAttribute("listCategories", listCategories);

        return "products/products";

    }


    @GetMapping("products/new")
    public String newProduct(Model model) {
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", brandService.listAllBrandsOnlyIdAndName());
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFiles,
                              @RequestParam(value = "detailIDs", required = false) Long[] detailIDs,
                              @RequestParam(value = "detailNames", required = false) String[] detailNames,
                              @RequestParam(value = "detailValues", required = false) String[] detailValues,
                              @RequestParam(value = "imageIDs", required = false) Long[] imageIDs,
                              @RequestParam(value = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal TechShopUserDetails loggedUser
    ) throws IOException, ProductNotFoundException {

//       ============= Sa el e ashxatum============
//        if (loggedUser.getUser().getRoles().contains(roleRepository.findByName("Salesperson"))){
//            productService.saveProductPrice(product);
//            redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
//            return "redirect:/products";
//        }


        if (!loggedUser.getUser().hasRole("Admin") && !loggedUser.getUser().hasRole("Editor")) {
            if (loggedUser.getUser().hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
                return "redirect:/products";
            }
        }

        setMainImageName(mainImageMultipartFile, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultipartFiles, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipartFile, extraImageMultipartFiles, savedProduct);
        deleteExtraImagesWereRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }


    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Long id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The Product Id:" + id + " has been " + status);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                Model model, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);

            String uploadExtraImageDir = "TechShopWebParent/product-images/" + id + "/extras";
            String uploadDir = "TechShopWebParent/product-images/" + id;

            FileUploadUtil.removeDir(uploadExtraImageDir);
            FileUploadUtil.removeDir(uploadDir);

            redirectAttributes.addFlashAttribute("message", "The product ID " +
                    id + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal TechShopUserDetails loggedUser) {
        try {
            Product product = productService.findById(id);
            boolean isReadOnlySalesperson = false;

            if (!loggedUser.getUser().hasRole("Admin") && !loggedUser.getUser().hasRole("Editor")) {
                if (loggedUser.getUser().hasRole("Salesperson")) {
                    isReadOnlySalesperson= true;
                }
            }
            model.addAttribute("isReadOnlySalesperson", isReadOnlySalesperson);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");
            model.addAttribute("listBrands", brandService.listAllBrandsOnlyIdAndName());
            return "products/product_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

}
