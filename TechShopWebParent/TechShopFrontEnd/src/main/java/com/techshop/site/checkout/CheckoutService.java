package com.techshop.site.checkout;

import com.techshop.common.entity.CartItem;
import com.techshop.common.entity.ShippingRate;
import com.techshop.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    public final static int DIM_DIVISOR = 139;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();
        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCostTotal(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);

        return checkoutInfo;
    }

    private float calculateShippingCostTotal(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * shippingRate.getRate()* cartItem.getQuantity();
            cartItem.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }
        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float productTotal = 0.0f;
        for (CartItem cartItem : cartItems) {
            productTotal += cartItem.getSubtotal();
        }
        return productTotal;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float productCost = 0.0f;
        for (CartItem cartItem : cartItems) {
             productCost += cartItem.getProduct().getCost() * cartItem.getQuantity();
        }
        return productCost;
    }
}
