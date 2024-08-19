package com.techshop.site.shipping;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ShippingRepositoryTests {

    @Autowired private ShippingRateRepository shippingRateRepository;

    @Test
    public void testFindByCountryIdAndState(){
        Long countryId = 12L;
        String state = "Shirak";
        Country country = new Country(countryId);
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(country, state);
        assertThat(shippingRate).isNotNull();
        System.out.println(shippingRate);
    }


}
