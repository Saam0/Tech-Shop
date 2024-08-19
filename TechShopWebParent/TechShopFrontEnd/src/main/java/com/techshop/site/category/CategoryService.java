package com.techshop.site.category;

import com.techshop.common.entity.Category;
import com.techshop.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategory(){
        List<Category> listNoChildrenCategory = new ArrayList<>();

        List<Category> listEnabledCategory = categoryRepository.findAllEnabled();
        listEnabledCategory.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children==null || children.size()==0){
                listNoChildrenCategory.add(category);
            }
        });
        return listNoChildrenCategory;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null){
            throw new CategoryNotFoundException("Could not find any category with alias " + alias);
        }
        return category;
    }

    public List<Category> getCategoryParents(Category children){
        List<Category> listParents = new ArrayList<>();
        Category parent = children.getParent();
        while (parent != null){
            listParents.add(0,parent);
            parent = parent.getParent();

        }
        listParents.add(children); // add children to the end of list
        return listParents;
    }

}
