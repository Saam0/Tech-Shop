package com.techshop.admin.brand;

import com.techshop.common.entity.Brand;
import com.techshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkUnique(@RequestParam(value = "id",required = false) Long id, @RequestParam("name") String name) {
        return brandService.checkUnique(id, name);

    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Long id) throws BrandNotFoundRestException {
        List<CategoryDTO> listCategories = new ArrayList<>();
        try {
            Brand brand = brandService.getById(id);
            Set<Category> categories = brand.getCategories();
            for (Category category : categories) {
                CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
                listCategories.add(dto);
            }

        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
        return listCategories;
    }

}
