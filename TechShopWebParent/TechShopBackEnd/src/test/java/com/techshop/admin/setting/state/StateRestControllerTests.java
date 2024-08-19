package com.techshop.admin.setting.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.admin.setting.country.CountryRepository;
import com.techshop.common.entity.Country;
import com.techshop.common.entity.State;
import com.techshop.common.entity.StateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StateRepository stateRepository;

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testListByCountry() throws Exception {
        String url = "/states/list_by_country/2";
        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        StateDTO[] states = objectMapper.readValue(jsonResponse, StateDTO[].class);

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testSaveState() throws Exception {
        String url = "/states/save";
        Long countryId = 1L;
        Country country = countryRepository.findById(countryId).get();
        State state = new State( "Volgograd", country);
        String jsonRequest = objectMapper.writeValueAsString(state);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json").content(jsonRequest).with(csrf()))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Long stateId = Long.parseLong(jsonResponse);
        assertThat(stateId).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testUpdateState() throws Exception {
        String url = "/states/save";
        Long stateId = 3L;
        State state = stateRepository.findById(stateId).get();
        state.setName("Vladivostok");
        String jsonRequest = objectMapper.writeValueAsString(state);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json").content(jsonRequest).with(csrf()))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Long id = Long.parseLong(jsonResponse);
        State updatedState = stateRepository.findById(id).get();
        assertThat(id).isGreaterThan(0);
        assertThat(updatedState.getName()).isEqualTo("Vladivostok");
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testDeleteState() throws Exception {
        String url = "/state/delete/3";
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());
        State state = stateRepository.findById(3L).orElse(null);
        assertThat(state).isNull();
    }
}
