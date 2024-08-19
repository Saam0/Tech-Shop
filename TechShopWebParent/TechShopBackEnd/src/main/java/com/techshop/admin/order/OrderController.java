package com.techshop.admin.order;

import com.techshop.admin.paging.PagingAndSortingHelper;
import com.techshop.admin.paging.PagingAndSortingParam;
import com.techshop.admin.setting.SettingService;
import com.techshop.common.entity.order.Order;
import com.techshop.common.entity.order.OrderDetail;
import com.techshop.common.entity.order.OrderStatus;
import com.techshop.common.entity.order.OrderTrack;
import com.techshop.common.entity.product.Product;
import com.techshop.common.entity.setting.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {

    private final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            HttpServletRequest request,
            Model model) {
        orderService.listByPage(pageNum, helper);
//        List<Order> listOrders = (List<Order>) request.getAttribute("listOrders");
//        model.addAttribute("listOrders", listOrders);
        loadCurrencySetting(request);
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> listSettings = settingService.getCurrencySetting();
        for (Setting setting : listSettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/order/detail/{id}")
    public String viewOrderDetails(@PathVariable(name = "id") Long id,
                                   Model model,
                                   HttpServletRequest request,
                                   RedirectAttributes ra) {
        try {
            Order order = orderService.get(id);
            model.addAttribute("order", order);
            loadCurrencySetting(request);
            return "orders/order_details_modal";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable(name = "id") Long id,
                              Model model,
                              RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order ID " + id + " has been deleted successfully.");
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable(name = "id") Long id,
                            Model model,
                            RedirectAttributes ra) {
        try {
            Order order = orderService.get(id);
            model.addAttribute("order", order);
            model.addAttribute("listCountries", orderService.listAllCountries());
            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");

            return "orders/order_form";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/order/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) throws OrderNotFoundException {
        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.save(order);
        ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully.");
        return defaultRedirectURL;
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackNotes = request.getParameterValues("trackNotes");
        String[] trackDates = request.getParameterValues("trackDate");

        List<OrderTrack> orderTracks = order.getOrderTracks();

        for (int i = 0; i < trackIds.length; i++) {
            System.err.println("trackId: " + trackIds[i]);
            System.err.println("trackStatus: " + trackStatuses[i]);
            System.err.println("trackNote: " + trackNotes[i]);
            System.err.println("trackDate: " + trackDates[i]);
            System.err.println("=====================================");

            OrderTrack orderTrack = new OrderTrack();
            Long trackId = Long.valueOf(trackIds[i]);
            if (trackId > 0) {
                orderTrack.setId(trackId);
            }
            orderTrack.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            orderTrack.setNotes(trackNotes[i]);
            orderTrack.setUpdatedTimeOnForm(trackDates[i]);
            orderTrack.setOrder(order);

            orderTracks.add(orderTrack);
        }
    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productNames = request.getParameterValues("productName");
        String[] quantities = request.getParameterValues("quantity");
        String[] unitPrices = request.getParameterValues("unitPrice");
        String[] productDetailCost = request.getParameterValues("productDetailCost");
        String[] subtotal = request.getParameterValues("subtotal");
        String[] shippingCosts = request.getParameterValues("shippingCost");

        for (int i = 0; i < detailIds.length; i++) {
            System.err.println("detailId: " + detailIds[i]);
            System.err.println("productId: " + productIds[i]);
            System.err.println("quantity: " + quantities[i]);
            System.err.println("unitPrice: " + unitPrices[i]);
            System.err.println("productCost: " + productDetailCost[i]);
            System.err.println("subtotal: " + subtotal[i]);
            System.err.println("ShippingCost: " + shippingCosts[i]);
            System.err.println("=====================================");

            OrderDetail orderDetail = new OrderDetail();
            Long detailId = Long.valueOf(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }
            orderDetail.setOrder(order);
            orderDetail.setProductCost(Float.parseFloat(productDetailCost[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setSubtotal(Float.parseFloat(subtotal[i]));
            orderDetail.setUnitPrice(Float.parseFloat(unitPrices[i]));
            orderDetail.setProduct(new Product(Long.parseLong(productIds[i])));
            orderDetail.setShippingCost(Float.parseFloat(shippingCosts[i]));
            order.getOrderDetails().add(orderDetail);
        }


    }
}
