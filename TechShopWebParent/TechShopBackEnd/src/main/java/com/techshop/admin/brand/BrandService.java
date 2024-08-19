package com.techshop.admin.brand;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BrandService {

    public static final int BRANDS_PER_PAGE = 4;
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> listAllBrandsOnlyIdAndName() {
        return brandRepository.findAllIncludeOnlyIdAndName();
    }

    public void findAllByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, BRANDS_PER_PAGE, brandRepository);
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand getById(Long id) throws BrandNotFoundException {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Could not find any brand with ID " + id));
    }

    public void delete(Long id) throws BrandNotFoundException {
        Brand byId = getById(id);
        brandRepository.deleteById(id);
    }

    public String checkUnique(Long id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = brandRepository.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "Duplicate";
        } else {
            if (brandByName != null && !Objects.equals(brandByName.getId(), id)) {
                return "Duplicate";
            }
        }
        return "OK";
    }

}
