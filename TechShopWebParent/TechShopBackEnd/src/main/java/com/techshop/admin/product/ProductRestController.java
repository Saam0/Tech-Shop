package com.techshop.admin.product;

import com.techshop.common.entity.product.Product;
import com.techshop.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products/check_unique")
    public String checkUnique(@RequestParam("id") Long id, @RequestParam("name") String name){
        return productService.checkUnique(id,name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProduct(@PathVariable("id") Long id) throws ProductNotFoundException {
        Product product = productService.findById(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
