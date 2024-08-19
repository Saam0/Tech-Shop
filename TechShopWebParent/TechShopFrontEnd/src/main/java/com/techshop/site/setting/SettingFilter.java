package com.techshop.site.setting;

import com.techshop.common.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.techshop.common.Constants.S3_BASE_URI;

@Component
public class SettingFilter implements Filter {
    @Autowired
    private SettingService settingService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURI();
        if (url.endsWith(".css")||url.endsWith(".js")||url.endsWith(".jpg")||url.endsWith(".png")||url.endsWith(".gif")||url.endsWith(".svg")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        List<Setting> settings = settingService.getGeneralSettings();
        for (Setting setting : settings) {
            request.setAttribute(setting.getKey(), setting.getValue());
            if (setting.getKey().equals("SITE_LOGO")) {
                request.setAttribute("SITE_LOGO", S3_BASE_URI + setting.getValue());
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
