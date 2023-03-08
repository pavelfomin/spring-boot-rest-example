package com.droidablebee.springboot.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * AbstractEndpointTest with common test methods.
 */
@AutoConfigureMockMvc //this creates MockMvc instance correctly, including wiring of the spring security
public abstract class BaseEndpointTest {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

	@MockBean
	protected JwtDecoder jwtDecoder;

	@Mock
	protected Jwt jwt;

	/**
	 * @BeforeEach methods are inherited from superclasses as long as they are not overridden.
	 * Hence, the different method name.
	 */
	@BeforeEach
	public void setupBase() {

		when(jwtDecoder.decode(anyString())).thenAnswer(
				invocation -> {
					String token = invocation.getArgument(0);
					if ("invalid".equals(token)) {
						throw new BadJwtException("Token is invalid");
					} else {
						return jwt;
					}
				}
		);
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

	protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithScope(String scope) {

		return jwt().jwt(jwt -> jwt.claims(claims -> claims.put("scope", scope)));
	}

}
