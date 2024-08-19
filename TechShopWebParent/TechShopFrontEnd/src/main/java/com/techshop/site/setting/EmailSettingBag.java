package com.techshop.site.setting;

import com.techshop.common.entity.setting.Setting;
import com.techshop.common.entity.setting.SettingBag;

import java.util.List;

public class EmailSettingBag extends SettingBag {
    public EmailSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getHost() {
        return this.getValue("MAIL_HOST");
    }

    public int getPort() {
        return Integer.parseInt(this.getValue("MAIL_PORT"));
    }

    public String getUsername() {
        return this.getValue("MAIL_USERNAME");
    }

    public String getPassword() {
        return this.getValue("MAIL_PASSWORD");
    }

    public String getSmtpAuth() {
        return this.getValue("SMTP_AUTH");
    }

    public String getSmtpSecured() {
        return this.getValue("SMTP_SECURED");
    }

    public String getFromAddress() {
        return this.getValue("MAIL_FROM");
    }

    public String getSenderName() {
        return this.getValue("MAIL_SENDER_NAME");
    }

    public String getCustomerVerifySubject() {
        return this.getValue("CUSTOMER_VERIFY_SUBJECT");
    }


    public String getCustomerVerifyContent() {
        return this.getValue("CUSTOMER_VERIFY_CONTENT");
    }

    public String getOrderConfirmationSubject() {
        return this.getValue("ORDER_CONFIRMATION_SUBJECT");
    }

    public String getOrderConfirmationContent() {
        return this.getValue("ORDER_CONFIRMATION_CONTENT");
    }


}
