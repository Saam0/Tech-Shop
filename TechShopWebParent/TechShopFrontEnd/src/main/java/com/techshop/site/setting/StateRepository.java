package com.techshop.site.setting;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.State;
import com.techshop.common.entity.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Long> {

    List<State> findByCountryOrderByNameAsc(Country country);

}