package com.techshop.site.checkout.paypal;

import java.util.Objects;

public class PayPalOrderResponse {
    private String id;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isValidate(String orderId) {
        return Objects.equals(this.id, orderId) && Objects.equals(this.status, "COMPLETED");
    }
}
