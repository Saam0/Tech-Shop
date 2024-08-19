package com.techshop.admin.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> listAllSetting(){
        return settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSettings(){
        List<Setting> listSettings = new ArrayList<>();
        List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        listSettings.addAll(generalSettings);
        listSettings.addAll(currencySettings);
        return new GeneralSettingBag(listSettings);
    }

    public void saveAll(List<Setting> listSettings){
        settingRepository.saveAll(listSettings);
    }

    public List<Setting> getMailServerSettings(){
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    public List<Setting> getMailTemplateSettings(){
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }

    public List<Setting> getCurrencySetting() {
        return settingRepository.findByCategory(SettingCategory.CURRENCY);
    }

    public List<Setting> getPaymentSetting() {
        return settingRepository.findByCategory(SettingCategory.PAYMENT);
    }
}
