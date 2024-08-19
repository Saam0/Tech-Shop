package com.techshop.admin.category;

import com.techshop.common.entity.Category;
import com.techshop.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CategoryService {
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository categoryRepository;


    public Category save(Category category) {
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIDs = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIDs += String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIDs);
        }


        return categoryRepository.save(category);
    }


    public List<Category> getAllCategoriesByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir == null || sortDir.isEmpty()) {
            sort = sort.ascending();
        } else if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;
        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = categoryRepository.searchCategory(keyword, pageable);
            List<Category> searchResultList = new ArrayList<>();
            for (Category category : pageCategories.getContent()) {
                if (category.getParent() == null) {
                    category.setChildren(new HashSet<>());
                    searchResultList.add(category);
                }
//                category.setHasChildren(category.getChildren().size()>0);
            }
            return searchResultList;
        } else {
            pageCategories = categoryRepository.getRootCategory(pageable);

        }
//        Page<Category> pageCategories = categoryRepository.findAll(pageable);
//        Page<Category> pageCategories = categoryRepository.getRootCategory(pageable);
        List<Category> categoryList = pageCategories.getContent();
        pageInfo.setTotalPages(pageCategories.getTotalPages());
        pageInfo.setTotalElements(pageCategories.getTotalElements());


        return categoryList;
    }

    public List<Category> getAllCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        for (Category category : categoryRepository.findAll(Sort.by("name").ascending())) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(Category.fullCopy(category));
                listSubCategoriesUsedInForm(categoriesUsedInForm, category, 0, null);
            }
        }
        return categoriesUsedInForm;
    }

    public List<Category> getAllHierarchicalCategories(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        List<Category> categoryList = new ArrayList<>();

        for (Category category : getAllCategoriesByPage(pageInfo, pageNum, sortDir, keyword)) {
//            System.err.println(category.getName());
            if (category.getParent() == null) {

                categoryList.add(Category.fullCopy(category));
                listSubCategoriesUsedInForm(categoryList, category, 0, sortDir);
            }
        }
        return categoryList;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel, String sortDir) {
        subLevel++;
        Set<Category> children = sortedSubCategories(parent.getChildren(), sortDir);
        for (Category subCategory : children) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < subLevel; i++) {
                name.append("--");
            }
            categoriesUsedInForm.add(Category.fullCopy(subCategory, name + subCategory.getName()));

            listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, subLevel, sortDir);
        }
    }

    private SortedSet<Category> sortedSubCategories(Set<Category> subCategories) {
        return sortedSubCategories(subCategories, "asc");
    }

    private SortedSet<Category> sortedSubCategories(Set<Category> subCategories, String sortDir) {

        SortedSet<Category> sortedSet = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir == null || sortDir.isEmpty()) {
                    return cat1.getName().compareTo(cat2.getName());
                } else if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });
        sortedSet.addAll(subCategories);
        return sortedSet;
    }

    public Category getById(Long id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Could not find any category with ID " + id));
    }

    public String checkUnique(Long id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);
        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }
            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }
        }
        return "OK";
    }


    public void updateCategoryEnabledStatus(Long id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    public void deleteById(Long id) throws CategoryNotFoundException {
        Category category = getById(id);
        categoryRepository.deleteById(id);
    }

}
