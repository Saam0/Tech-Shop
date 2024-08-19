package com.techshop.admin.setting.state;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Long> {

    List<State> findByCountryOrderByNameAsc(Country country);
}