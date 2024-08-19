package com.techshop.site.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingBag;

import java.util.List;

public class CurrencySettingBag extends SettingBag {

    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public CurrencySettingBag() {
    }

    public String getCurrencySymbol() {
       return this.getValue("CURRENCY_SYMBOL");
   }

    public String getCurrencySymbolPosition() {
         return this.getValue("CURRENCY_SYMBOL_POSITION");
    }

//    getDecimalPointType
    public String getDecimalPointType() {
         return this.getValue("DECIMAL_POINT_TYPE");
    }

//    getThousandsPointType
    public String getThousandsPointType() {
         return this.getValue("THOUSANDS_POINT_TYPE");
    }

//    getDecimalDigits
    public int getDecimalDigits() {
         return Integer.parseInt(super.getValue("DECIMAL_DIGITS")) ;
    }


}
