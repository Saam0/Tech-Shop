package com.techshop.admin.shippingrate;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShippingRateController {
    private final String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";

    @Autowired
    private ShippingRateService shippingRateService;

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "shippingRates",
            moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {
        shippingRateService.listByPage(pageNum, helper);
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newShippingRate(Model model) {
        List<Country> listAllCountries = shippingRateService.listAllCountries();
        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listAllCountries);
        model.addAttribute("pageTitle", "Create New Shipping Rate");
        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveShippingRate(ShippingRate rate, Model model, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.save(rate);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully.");
        } catch (ShippingRateAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editShippingRate(@PathVariable(name = "id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ShippingRate rate = shippingRateService.get(id);
            List<Country> listAllCountries = shippingRateService.listAllCountries();
            model.addAttribute("rate", rate);
            model.addAttribute("listCountries", listAllCountries);
            model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");
            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    //create a method to handle the request to update COD support
    @GetMapping("/shipping_rates/{id}/cod_support/{supported}")
    public String updateCODSupport(@PathVariable(name = "id") Long id,
                                   @PathVariable(name = "supported") Boolean supported,
                                   RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.updateCODSupport(id, supported);
            String status = supported ? "enabled" : "disabled";
            String message = "The COD support for shipping rate ID " + id + " has been " + status;
            redirectAttributes.addFlashAttribute("message", message);
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    //create delete method
    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteShippingRate(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted successfully");
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }
}
