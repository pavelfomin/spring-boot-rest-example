package com.droidablebee.springboot.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * AbstractEndpointTest with common test methods.
 */
@AutoConfigureMockMvc //this creates MockMvc instance correctly, including wiring of the spring security
public abstract class BaseEndpointTest {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

	@Autowired
    protected WebApplicationContext webApplicationContext;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

//    protected void setup() throws Exception {
//
//    	this.mockMvc = webAppContextSetup(webApplicationContext).build();
//    }
    
	/**
	 * Returns json representation of the object.
	 * @param o instance
	 * @return json
	 * @throws IOException
	 */
	protected String json(Object o) throws IOException {

		return objectMapper.writeValueAsString(o);
	}

	protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithScope(String scope) {

		return jwt().jwt(jwt -> jwt.claims(claims -> claims.put("scope", scope)));
	}

}
