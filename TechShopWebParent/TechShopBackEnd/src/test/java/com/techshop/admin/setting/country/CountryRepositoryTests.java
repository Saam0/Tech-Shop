package com.techshop.admin.setting.country;

import com.techshop.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTests {
    @Autowired
    public CountryRepository countryRepository;

    @Test
    public void testCreateCountries() {
        Country France = countryRepository.save(new Country("France", "FR"));
        Country Germany = countryRepository.save(new Country("Germany", "DE"));
        Country Italy = countryRepository.save(new Country("Italy", "IT"));
        Country Spain = countryRepository.save(new Country("Spain", "ES"));
        Country UnitedKingdom = countryRepository.save(new Country("United Kingdom", "UK"));
        Country UnitedStates = countryRepository.save(new Country("United States", "US"));
        Country Canada = countryRepository.save(new Country("Canada", "CA"));
        Country Mexico = countryRepository.save(new Country("Mexico", "MX"));
        Country Brazil = countryRepository.save(new Country("Brazil", "BR"));
        Country Argentina = countryRepository.save(new Country("Argentina", "AR"));
        Country China = countryRepository.save(new Country("China", "CN"));
        Country Japan = countryRepository.save(new Country("Japan", "JP"));
        Country SouthKorea = countryRepository.save(new Country("South Korea", "KR"));
        Country India = countryRepository.save(new Country("India", "IN"));
        Country Australia = countryRepository.save(new Country("Australia", "AU"));
        Country Armenia = countryRepository.save(new Country("Armenia", "AM"));

        List<Country> countries = countryRepository.saveAll(List.of(France, Germany, Italy, Spain, UnitedKingdom, UnitedStates, Canada, Mexico, Brazil, Argentina, China, Japan, SouthKorea, India, Australia, Armenia));

        assertThat(countries.size()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> allCountry = countryRepository.findAllByOrderByNameAsc();
        allCountry.forEach(System.out::println);
        assertThat(allCountry.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Country country = countryRepository.findById(1L).get();
        country.setName("Russia");
        country.setCode("RU");
        Country updetedCountry = countryRepository.save(country);
        assertThat(updetedCountry.getName()).isEqualTo("Russia");
    }

    @Test
    public void testDeleteCountry() {
        Long id = 5L;
        countryRepository.deleteById(id);
        assertThat(countryRepository.findById(id)).isEmpty();
    }


}
