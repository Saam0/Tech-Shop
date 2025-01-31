package com.techshop.site.checkout.paypal;

import com.techshop.site.setting.PaymentSettingBag;
import com.techshop.site.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PayPalService {
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Autowired
    private SettingService settingService;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);

        return orderResponse.isValidate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwExceptionForNonOKResponse(statusCode);
        }
        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseURL = paymentSettings.getURL();
        String clientId = paymentSettings.getClientId();
        String clientSecret = paymentSettings.getClientSecret();
        String requestUrl = baseURL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
    }

    private static void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = null;
        switch (statusCode) {
            case NOT_FOUND:
                message = "Order ID not found";
            case BAD_REQUEST:
                message = "Bad request to PayPal API";
            case INTERNAL_SERVER_ERROR:
                message = "PayPal server error";
            default:
                message = "PayPal returned non-OK status code";
        }

        throw new PayPalApiException(message);
    }
}
