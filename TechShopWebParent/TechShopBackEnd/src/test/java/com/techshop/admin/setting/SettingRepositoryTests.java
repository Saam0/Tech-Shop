package com.techshop.admin.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTests {
    @Autowired
    public SettingRepository settingRepository;

//    @Test
//    public void testCreateGeneralSetting(){
//        Setting siteName = new Setting("SITE_NAME","Tech-Shop", SettingCategory.GENERAL);
//        Setting savedSetting = settingRepository.save(siteName);
//        assertThat(savedSetting).isNotNull();
//    }

    @Test
    public void testCreateGeneralSetting() {
        Setting siteLogo = new Setting("SITE_LOGO", "Logo.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2023 tech-shop Ltd", SettingCategory.GENERAL);
        settingRepository.saveAll(List.of(siteLogo, copyright));
        List<Setting> all = settingRepository.findAll();
        assertThat(all).size().isGreaterThan(0);
    }

    @Test
    public void testCreateCurrencySetting() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "point", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        settingRepository.saveAll(List.of(currencyId, symbol,symbolPosition,decimalPointType,thousandsPointType));
        List<Setting> all = settingRepository.findAll();
    }

    @Test
    public void testListSettingByCategory() {
        List<Setting> all = settingRepository.findByCategory(SettingCategory.GENERAL);
        all.forEach(System.out::println);
        assertThat(all).size().isGreaterThan(0);
    }



}
