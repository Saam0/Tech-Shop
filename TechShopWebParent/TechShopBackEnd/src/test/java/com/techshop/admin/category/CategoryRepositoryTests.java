package com.techshop.admin.category;

import com.techshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Electronics");
        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0L);
    }

    @Test
    public void testCreateSubCategory(){
        Category parent = new Category(7L);
        Category memories=new Category("Iphone",parent);

        categoryRepository.save(memories);
        assertThat(memories.getId()).isGreaterThan(0);

    }

    @Test
    public void testGetCategory(){
       Category category = categoryRepository.findById(2L).get();
        Set<Category> children = category.getChildren();
        for (Category subCategory: children){
            System.out.println(subCategory.getName());
        }

        assertThat(children.size()).isGreaterThan(0);

    }

    @Test
    public void testPrintHierarchicalCategories(){
        List<Category> categories = categoryRepository.findAll();

        for (Category category:categories){
            if (category.getParent()==null){
                System.err.println(category.getName());
                printChildren(category,0);
//                Set<Category> children = category.getChildren();
//                for (Category subCategory:children){
//                    System.err.println("--"+subCategory.getName());
//                    printChildren(subCategory,1);
//                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel){
        subLevel++;
        Set<Category> children = parent.getChildren();
        for (Category subCategory: children) {
            for (int i = 0; i < subLevel; i++) {
                System.err.print("--");
            }
            System.err.println(subCategory.getName());
            printChildren(subCategory,subLevel);
        }

    }

    @Test
    public void testGetRootCategories(){
        List<Category> rootCategory = categoryRepository.getRootCategory(Sort.by("name").ascending());
        rootCategory.forEach(category -> System.err.println(category.getName()));
    }

    @Test
    public void testFindByName(){
        String name = "Computers";
        Category category = categoryRepository.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testFindByAlias(){
        String alias = "gaming_laptops";
        Category category = categoryRepository.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }
}