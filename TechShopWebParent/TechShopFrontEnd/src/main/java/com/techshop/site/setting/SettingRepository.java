package com.techshop.site.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
    @Query("SELECT s from Setting s WHERE s.category=?1 OR s.category=?2")
    List<Setting> findByTwoCategory(SettingCategory catOne, SettingCategory catTwo);

    Setting findByKey(String key);

}