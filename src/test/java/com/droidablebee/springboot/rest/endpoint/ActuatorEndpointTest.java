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
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class ActuatorEndpointTest extends BaseEndpointTest {

    @Before
    public void setup() throws Exception {

    	super.setup();
    }

    @Test
    public void getInfo() throws Exception {
    	
    	MvcResult result = mockMvc.perform(get("/info"))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.build", isA(Object.class)))
    	.andExpect(jsonPath("$.build.version", isA(String.class)))
    	.andExpect(jsonPath("$.build.artifact", is("spring-boot-rest-example")))
    	.andExpect(jsonPath("$.build.group", is("com.droidablebee")))
    	.andExpect(jsonPath("$.build.time", isA(String.class)))
    	.andReturn()
    	;
    	
    	logger.debug("content="+ result.getResponse().getContentAsString());
    }

}
