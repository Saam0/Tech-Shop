package com.techshop.site;

import com.techshop.site.setting.CurrencySettingBag;
import com.techshop.site.setting.EmailSettingBag;
import com.techshop.site.security.oauth.CustomerOAuth2User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag setting) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(setting.getHost());
        mailSender.setPort(setting.getPort());

        mailSender.setUsername(setting.getUsername());
        mailSender.setPassword(setting.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", setting.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", setting.getSmtpSecured());
        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    public static String getEmailOfAuthenticationToken(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal == null) return null;
        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            customerEmail = oAuth2User.getEmail();
        }
        return customerEmail;
    }

    public static String formatCurrency(float amount, CurrencySettingBag settings) {
        String symbol = settings.getCurrencySymbol();
        String symbolPosition = settings.getCurrencySymbolPosition();
        String decimalPointType = settings.getDecimalPointType();
        String thousandsPointType = settings.getThousandsPointType();
        int decimalDigits = settings.getDecimalDigits();

        String pattern = symbolPosition.equals("Before price") ? symbol  : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int i = 0; i < decimalDigits; i++) {
                pattern += "#";
            }
        }

        pattern += symbolPosition.equals("After price") ? symbol  : "";

        char decimalPoint = decimalPointType.equals("POINT") ? '.' : ',';
        char thousandsPoint = thousandsPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalPoint);
        decimalFormatSymbols.setGroupingSeparator(thousandsPoint);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
        return formatter.format( amount);
    }

}
