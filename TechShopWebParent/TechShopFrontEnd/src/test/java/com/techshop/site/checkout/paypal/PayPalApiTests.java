package com.techshop.site.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID="AREbGkFtxSiMPUGDbXZLjUqlHBem54fVyVSKKztk4UASivuS2yyB7DqqwNex09xxzlKDabDucvTKiBGC";
    private static final String CLIENT_SECRET="EAhKjxv0AtTphj-Gs2h2twEawJwDjKPkmdrAdhf5SFUXho_or6a2Quha3tPEEERyaw8PUhZiDY4ZiAt9";


    @Test
    public void testGetOrderDetails() {

        String orderId = "4SS231628A385413B";
        String requestUrl = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse responseBody = response.getBody();
        System.out.println("Order Id: " + responseBody.getId());
        System.out.println("Order Status: " + responseBody.isValidate(orderId));


    }


}
