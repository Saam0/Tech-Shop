package com.techshop.admin.setting;

import com.techshop.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findAllByOrderByNameAsc();
}