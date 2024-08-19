package com.techshop.site.category;

import com.techshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void testListEnabledCategories(){
         List<Category> categories = categoryRepository.findAllEnabled();
        categories.forEach(System.out::println);
    }

    // find by alias when it is enabled
    @Test
    public void testFindByAliasEnabled(){
        String alias = "electronics";
        Category category = categoryRepository.findByAliasEnabled(alias);
        assertThat(category).isNotNull();
    }

}
