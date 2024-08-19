package com.techshop.admin.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingBag;

import java.util.List;

public class GeneralSettingBag extends SettingBag {
    public GeneralSettingBag() {
    }

    public GeneralSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public void updateCurrencySymbol(String currencySymbol) {
        update("CURRENCY_SYMBOL", currencySymbol);
    }

    //create updateSiteLogo
    public void updateSiteLogo(String siteLogo) {
        update("SITE_LOGO", siteLogo);
    }



}
