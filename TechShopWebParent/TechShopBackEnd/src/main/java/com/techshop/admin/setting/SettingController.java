package com.techshop.admin.setting;

import com.techshop.admin.AmazonS3Util;
import com.techshop.admin.FileUploadUtil;
import com.techshop.common.entity.Currency;
import com.techshop.common.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.techshop.common.Constants.S3_BASE_URI;

@Controller
public class SettingController {

    @Autowired
    private SettingService settingService;
    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.listAllSetting();
        model.addAttribute("listCurrencies", currencyRepository.findAllByOrderByNameAsc());
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
            if (setting.getKey().equals("SITE_LOGO")) {
                model.addAttribute("SITE_LOGO", S3_BASE_URI + setting.getValue());

            }
        }
//        model.addAttribute("S3_BASE_URI", S3_BASE_URI + "/site-logo/");
        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSetting(@RequestParam("fileImage") MultipartFile multipartFile,
                                     HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        saveSiteLogo(multipartFile, settingBag);

        saveCurrencySymbol(request, settingBag);


        updateSettingValueFromForm(request, settingBag.list());


        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
        Long currencyId = Long.valueOf(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    private static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            String uploadDir = "site-logo";
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

//            String uploadDir = "TechShopWebParent/site-logo";
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSetting(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> listSettings = settingService.getMailServerSettings();
        updateSettingValueFromForm(request, listSettings);
        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplateSetting(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> listSettings = settingService.getMailTemplateSettings();
        updateSettingValueFromForm(request, listSettings);
        redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/save_payment")
    public String savePaymentSetting(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> paymentSettings = settingService.getPaymentSetting();
        updateSettingValueFromForm(request, paymentSettings);
        redirectAttributes.addFlashAttribute("message", "Payment settings have been saved.");
        return "redirect:/settings";
    }


}
