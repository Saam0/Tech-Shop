package com.techshop.admin.paging;

import com.techshop.admin.user.UserService;
import com.techshop.common.entity.Brand;
import com.techshop.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {

    private ModelAndViewContainer model;
    private String listName;
    private String sortField;
    private String sortDir;
    private String keyword;



    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortField, String sortDir, String keyword) {
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void updateModelAttributes(int pageNum, Page<?> page) {
        List<?> listItems = page.getContent();
        int pageSize = page.getSize();

        long startCount = (long) (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);
    }

    public void listEntities(int pageNum, int pageSize, SearchRepository<?,Long> repository) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc")?sort.ascending():sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1,pageSize,sort);
        Page<?> page = null;
        if (keyword!=null){
            page  = repository.findAll(keyword,pageable);
        }else {
            page  = repository.findAll(pageable);
        }
        updateModelAttributes(pageNum,page);
    }

    public Pageable createPageable(int pageNum, int pageSize) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc")?sort.ascending():sort.descending();
        return PageRequest.of(pageNum-1,pageSize,sort);
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public String getKeyword() {
        return keyword;
    }
}
