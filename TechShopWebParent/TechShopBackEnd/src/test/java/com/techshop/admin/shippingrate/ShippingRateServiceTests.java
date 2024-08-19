package com.techshop.admin.shippingrate;

import com.techshop.admin.product.ProductRepository;
import com.techshop.common.entity.ShippingRate;
import com.techshop.common.entity.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTests {

    @MockBean private ShippingRateRepository shippingRateRepository;
    @MockBean private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateService shippingRateService;

    @Test
    public void testCalculateShippingCost_NoRateFound() {
        Long productId = 1L;
        Long countryId = 2L;
        String state = "ABCDEF";

        Mockito.when(shippingRateRepository.findByCountryIdAndState(countryId, state)).thenReturn(null);

        Assertions.assertThrows(ShippingRateNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                shippingRateService.calculateShippingCost(productId, countryId, state);
            }
        }) ;

    }

    @Test
    public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
        Long productId = 1L;
        Long countryId = 2L;
        String state = "California";

        ShippingRate rate = new ShippingRate();
        rate.setRate(10.0f);

        Mockito.when(shippingRateRepository.findByCountryIdAndState(countryId, state)).thenReturn(rate);

        Product product = new Product();
        product.setLength(10.0f);
        product.setWidth(10.0f);
        product.setHeight(10.0f);
        product.setWeight(10.0f);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        assertEquals(100.0f, shippingCost);

    }
}
