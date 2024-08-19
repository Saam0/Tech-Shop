package com.techshop.admin.order;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.setting.country.CountryRepository;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private static final int ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CountryRepository countryRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();

        Sort sort = null;
        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("city")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        Page<Order> page = null;
        if (keyword != null && !keyword.isEmpty()) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Order get(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Could not find any orders with ID " + id));

    }

    public void delete(Long id) throws OrderNotFoundException {
        Long countById = orderRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) throws OrderNotFoundException {
        Order orderInDB = get(orderInForm.getId());
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }


}
