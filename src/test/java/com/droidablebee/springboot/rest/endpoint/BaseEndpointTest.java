package com.droidablebee.springboot.rest.endpoint;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AbstractEndpointTest with common test methods.
 */
public abstract class BaseEndpointTest {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

	@Autowired
    protected WebApplicationContext webApplicationContext;

	@Autowired
	ObjectMapper objectMapper;
	
	protected MockMvc mockMvc;

    protected void setup() throws Exception {

    	this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }
    
	/**
	 * Returns json representation of the object.
	 * @param o instance
	 * @return json
	 * @throws IOException
	 */
	protected String json(Object o) throws IOException {

		return objectMapper.writeValueAsString(o);
	}

}
