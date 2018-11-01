package com.droidablebee.springboot.rest.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles({"dev"})
public class ConfigEndpointTest extends BaseEndpointTest {

    @Before
    public void setup() throws Exception {

        super.setup();
    }

    @Test
    public void getConfig() throws Exception {

        mockMvc.perform(get("/v1/config"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.url", is("https://google.com")))
                .andExpect(jsonPath("$.port", is("443")))
        ;
    }
}
