package com.techshop.site.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingBag;

import java.util.List;

public class PaymentSettingBag extends SettingBag {

    public PaymentSettingBag() {
    }

    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getURL() {
        return this.getValue("PAYPAL_API_BASE_URL");
    }

    public String getClientId() {
        return this.getValue("PAYPAL_API_CLIENT_ID");
    }
    public String getClientSecret() {
        return this.getValue("PAYPAL_API_CLIENT_SECRET");
    }


}
