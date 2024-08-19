package com.techshop.admin.setting.country;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.techshop.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testSaveCountry() throws Exception {
        String url = "/countries/save";
        Country country = new Country("Georgia", "GE");
        String jsonRequest = objectMapper.writeValueAsString(country);
        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json").content(jsonRequest).with(csrf())).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Long countryId = Long.parseLong(jsonResponse);
        Country savedCountry = countryRepository.findById(countryId).get();
        assertThat(savedCountry.getName()).isEqualTo("Georgia");
        assertThat(countryId).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Long countryId = 4L;
        Country country = new Country(countryId,"Spain", "SP");
        String jsonRequest = objectMapper.writeValueAsString(country);
        mockMvc.perform(post(url).contentType("application/json").content(jsonRequest).with(csrf())).andExpect(status().isOk())
                .andDo(print()).andExpect(content().string(String.valueOf(countryId)));


        Country savedCountry = countryRepository.findById(countryId).get();
        assertThat(savedCountry.getName()).isEqualTo("Spain");
        assertThat(countryId).isGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testDeleteCountry() throws Exception {
        String url = "/countries/delete/4";
        mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print());
        assertThat(countryRepository.findById(4L).isPresent()).isFalse();
    }


}
