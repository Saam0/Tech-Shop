package com.techshop.admin.setting.state;

import com.techshop.admin.setting.country.CountryRepository;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTests {
    @Autowired
    public StateRepository stateRepository;
    @Autowired
    public CountryRepository countryRepository;


    @Test
    public void testCreateStatesInRussia() {

        Country country = countryRepository.findById(1L).get();
        State moscow = stateRepository.save(new State("Moscow", country));
        State saintPetersburg = stateRepository.save(new State("Saint Petersburg", country));
        State novosibirsk = stateRepository.save(new State("Novosibirsk", country));
        State yekaterinburg = stateRepository.save(new State("Yekaterinburg", country));
        State nizhnyNovgorod = stateRepository.save(new State("Nizhny Novgorod", country));
        State samara = stateRepository.save(new State("Samara", country));
        assertThat(moscow).isNotNull();
    }

    @Test
    public void testCreateStatesInFrance() {

        Country country = countryRepository.findById(2L).get();
        State paris = stateRepository.save(new State("Paris", country));
        State marseille = stateRepository.save(new State("Marseille", country));
        State lyon = stateRepository.save(new State("Lyon", country));
        State toulouse = stateRepository.save(new State("Toulouse", country));
        State nice = stateRepository.save(new State("Nice", country));
        State nantes = stateRepository.save(new State("Nantes", country));
        assertThat(paris).isNotNull();
    }

    //create  testListStatesByCountry
    @Test
    public void testListStatesByCountry() {
        Country country = countryRepository.findById(2L).get();
        Iterable<State> states = stateRepository.findByCountryOrderByNameAsc(country);
        states.forEach(System.out::println);
        assertThat(states).isNotNull();
    }

    //create  testUpdateState
    @Test
    public void testUpdateState() {
        State state = stateRepository.findById(1L).get();
        state.setName("Rostov-on-Don");
        State updetedState = stateRepository.save(state);
        assertThat(updetedState.getName()).isEqualTo("Rostov-on-Don");
    }

    @Test
    public void testDeleteState(){
        stateRepository.deleteById(1L);
        assertThat(stateRepository.findById(1L)).isEmpty();
    }

}
