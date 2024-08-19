package com.techshop.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkUnique(@RequestParam(value = "id",required = false) Long id, @RequestParam("name") String name, @RequestParam("alias") String alias){
        return categoryService.checkUnique(id,name,alias);
    }
}
