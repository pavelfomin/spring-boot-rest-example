package com.droidablebee.springboot.rest.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SwaggerEndpointTest extends BaseEndpointTest {

    @Test
    public void getApiDocs() throws Exception {

        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.openapi", is("3.0.1")))
                .andExpect(jsonPath("$.info", isA(Object.class)))
                .andExpect(jsonPath("$.servers", isA(Object.class)))
                .andExpect(jsonPath("$.paths", isA(Object.class)))
                .andExpect(jsonPath("$.components", isA(Object.class)))
                .andReturn();

        logger.debug("content=" + result.getResponse().getContentAsString());
    }

    @Test
    public void getApiDocSwaggerConfig() throws Exception {

        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.configUrl", isA(String.class)))
                .andExpect(jsonPath("$.oauth2RedirectUrl", isA(String.class)))
                .andExpect(jsonPath("$.url", isA(String.class)))
                .andExpect(jsonPath("$.validatorUrl", isA(String.class)))
        ;
    }

    @Test
    public void getSwaggerHtml() throws Exception {

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound())
        ;
    }

    @Test
    public void getSwaggerHtmlIndex() throws Exception {

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
        ;
    }

}
