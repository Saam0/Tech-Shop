package com.techshop.admin.product;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.common.entity.product.Product;
import com.techshop.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 4;

    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public void findAllByPage(int pageNum, PagingAndSortingHelper helper, Long categoryId) {
        Pageable pageable = helper.createPageable(pageNum, PRODUCTS_PER_PAGE);
        String keyword = helper.getKeyword();
        Page<Product> page = null;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            } else {
                page = productRepository.findAll(keyword, pageable);
            }

        } else {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page = productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(pageNum, PRODUCTS_PER_PAGE);
        String keyword = helper.getKeyword();
        Page<Product> page = productRepository.searchProductByName(keyword, pageable);
        helper.updateModelAttributes(pageNum, page);
    }

    public Product productByName(String name) {
        return productRepository.findByName(name);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }


        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            product.setAlias(product.getName().replaceAll(" ", "-"));
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }

    public void saveProductPrice(Product productInForm) throws ProductNotFoundException {
        Product productDB = findById(productInForm.getId());
        productDB.setPrice(productInForm.getPrice());
        productDB.setCost(productInForm.getCost());
        productDB.setDiscountPercent(productInForm.getDiscountPercent());
        productRepository.save(productDB);
    }


    public String checkUnique(Long id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        if (isCreatingNew) {
            if (productByName(name) != null) return "Duplicate";
        } else {
            if (productByName(name) != null && productByName(name).getId() != id) return "Duplicate";
        }
        return "OK";
    }

    public void updateProductEnabledStatus(Long id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Long id) throws ProductNotFoundException {
        Product productById = findById(id);
        productRepository.deleteById(id);

    }

    public Product findById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find any product with ID " + id));
    }

}
