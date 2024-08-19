package com.techshop.site.category;

import com.techshop.common.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.enabled = true Order By c.name ASC")
    List<Category> findAllEnabled();

    //find by  alias when it is enabled
    @Query("SELECT c FROM Category c WHERE c.alias = ?1 AND c.enabled = true")
    Category findByAliasEnabled(String alias);

}