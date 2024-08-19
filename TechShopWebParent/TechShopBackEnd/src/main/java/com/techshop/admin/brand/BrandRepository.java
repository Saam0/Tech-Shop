package com.techshop.admin.brand;

import com.techshop.admin.paging.SearchRepository;
import com.techshop.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends SearchRepository<Brand, Long> {
    Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);

    @Query("SELECT NEW Brand (b.id,b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAllIncludeOnlyIdAndName();
}