package com.techshop.admin.category;

import com.techshop.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL")
    List<Category> getRootCategory(Sort sort);

    @Query("SELECT c FROM Category c WHERE c.parent.id IS NULL")
    Page<Category> getRootCategory(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> searchCategory(String keyword,Pageable pageable);

    Category findByName(String name);

    Category findByAlias(String alias);

    @Query("UPDATE Category c SET c.enabled=?2 WHERE c.id = ?1")
    @Modifying
    void updateEnabledStatus(Long id, boolean enabled);


}