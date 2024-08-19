package com.techshop.admin.setting;

import com.techshop.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testCreateCurrency() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("United States dollar", "$", "USD"));
        currencies.add(new Currency("Euro", "€", "EUR"));
        currencies.add(new Currency("Japanese yen", "¥", "JPY"));
        currencies.add(new Currency("Pound sterling", "£", "GBP"));
        currencies.add(new Currency("Australian dollar", "$", "AUD"));
        currencies.add(new Currency("Canadian dollar", "$", "CAD"));
        currencies.add(new Currency("Swiss franc", "Fr", "CHF"));
        currencies.add(new Currency("Chinese yuan", "¥", "CNY"));
        currencies.add(new Currency("Hong Kong dollar", "$", "HKD"));
        currencies.add(new Currency("South Korean won", "₩", "KRW"));
        currencies.add(new Currency("Mexican peso", "$", "MXN"));
        currencies.add(new Currency("Norwegian krone", "kr", "NOK"));
        currencies.add(new Currency("Russian ruble", "₽", "RUB"));
        currencies.add(new Currency("Indian rupee", "₹", "INR"));
        currencies.add(new Currency("Brazilian real", "R$", "BRL"));
        currencies.add(new Currency("South African rand", "R", "ZAR"));
        currencies.add(new Currency("Danish krone", "kr", "DKK"));
        currencies.add(new Currency("Armenian dram", "֏", "AMD"));

        currencyRepository.saveAll(currencies);
        List<Currency> all = currencyRepository.findAll();
        assertThat(all).size().isEqualTo(18);
    }

    @Test
    public void testListAllOrderByNameAsc(){
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        currencies.forEach(System.out::println);
        assertThat(currencies.size()).isGreaterThan(0);

    }
}
