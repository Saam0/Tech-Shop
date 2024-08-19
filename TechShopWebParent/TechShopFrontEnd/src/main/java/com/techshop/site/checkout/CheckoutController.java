package com.techshop.site.checkout;

import com.techshop.common.entity.Address;
import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.Customer;
import com.techshop.common.entity.ShippingRate;
import com.techshop.common.entity.order.Order;
import com.techshop.common.entity.order.OrderStatus;
import com.techshop.common.entity.order.PaymentMethod;
import com.techshop.site.Utility;
import com.techshop.site.address.AddressService;
import com.techshop.site.checkout.paypal.PayPalApiException;
import com.techshop.site.checkout.paypal.PayPalService;
import com.techshop.site.customer.CustomerService;
import com.techshop.site.order.OrderService;
import com.techshop.site.setting.EmailSettingBag;
import com.techshop.site.setting.PaymentSettingBag;
import com.techshop.site.setting.SettingService;
import com.techshop.site.shipping.ShippingRateService;
import com.techshop.site.shoppingcart.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private CheckoutService checkoutService;
    @Autowired private CustomerService customerService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shippingRateService;
    @Autowired private ShoppingCartService shoppingCartService;
    @Autowired private OrderService orderService;
    @Autowired private SettingService settingService;
    @Autowired private PayPalService payPalService;

    @GetMapping("/checkout")
    public String viewCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String clientId = paymentSettings.getClientId();


        model.addAttribute("paypalClientId", clientId);
        model.addAttribute("customer", customer);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("pageTitle", "Checkout");


        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticationToken(request);
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
//        createdOrder.setStatus(OrderStatus.NEW);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, createdOrder);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = Utility.getEmailOfAuthenticationToken(request);
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());
        String totalAmount = Utility.formatCurrency(order.getTotal(), settingService.getCurrencySettings());

        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getShippingAddress());
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
        content = content.replace("[[total]]", totalAmount);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model)
            throws MessagingException, UnsupportedEncodingException {
        String orderId = request.getParameter("orderId");
        String pageTitle = "Checkout Failure";
        String message= null;
        try {
            if (payPalService.validateOrder(orderId)) {
                return placeOrder(request);
            } else {
                message = "ERROR: Transaction could not be completed because order information is invalid";
            }
        } catch (PayPalApiException e) {
             message = "ERROR: Transaction failed due to error: "+e.getMessage();
        }
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);
        return "message";
    }

}
