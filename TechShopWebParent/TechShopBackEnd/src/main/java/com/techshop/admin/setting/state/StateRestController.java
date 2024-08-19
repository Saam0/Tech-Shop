package com.techshop.admin.setting.state;

import com.techshop.common.entity.Country;
import com.techshop.common.entity.State;
import com.techshop.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/states/list_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Long id) {
        List<StateDTO> list = new ArrayList<>();
        stateRepository.findByCountryOrderByNameAsc(new Country(id)).forEach(state -> {
            list.add(new StateDTO(state.getId(), state.getName()));
        });
        return list;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state){
        State savedState = stateRepository.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/state/delete/{id}")
    public void delete(@PathVariable("id") Long id){
        stateRepository.deleteById(id);
    }
}
