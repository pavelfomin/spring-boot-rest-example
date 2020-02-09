package com.droidablebee.springboot.rest.endpoint;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SwaggerEndpointTest extends BaseEndpointTest {

    @Test
    public void getApiDocs() throws Exception {
    	
    	MvcResult result = mockMvc.perform(get("/v2/api-docs"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.swagger", is("2.0")))
    	.andExpect(jsonPath("$.info", isA(Object.class)))
    	.andExpect(jsonPath("$.paths", isA(Object.class)))
    	.andExpect(jsonPath("$.definitions", isA(Object.class)))
    	.andReturn()
    	;
    	
    	logger.debug("content="+ result.getResponse().getContentAsString());
    }

    @Test
    public void getSwaggerHtml() throws Exception {
    	
    	mockMvc.perform(get("/swagger-ui.html"))
    			.andExpect(status().isOk())
    			;
    }
    
}
