package com.techshop.admin.order;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.admin.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductSearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/orders/search_product")
    public String searchProduct() {
        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
    public String searchProducts(@Param("keyword") String keyword, Model model) {
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }


    @GetMapping("/orders/search_product/page/{pageNum}")
    public String searchProductsByPage(@PagingAndSortingParam(listName = "listProducts", moduleURL = "/orders/search_product") PagingAndSortingHelper helper,
                                       @PathVariable(name = "pageNum") int pageNum) {
        productService.searchProducts(pageNum, helper);
        return "orders/search_product";
    }
}
