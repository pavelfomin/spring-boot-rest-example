package com.droidablebee.springboot.rest.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * AbstractEndpointTest with common test methods.
 */
@AutoConfigureMockMvc //this creates MockMvc instance correctly, including wiring of the spring security
@ExtendWith(MockitoExtension.class) //  MockitoTestExecutionListener has been removed in SB 4
public abstract class BaseEndpointTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype());

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    // use @ExtendWith(MockitoExtension.class) since MockitoTestExecutionListener has been removed in SB 4
    // or `@MockitoBean` can be used w/out MockitoExtension
    @Mock
    protected Jwt jwt;

    /**
     * `@BeforeEach` methods are inherited from superclasses as long as they are not overridden.
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
     *
     * @param o instance
     * @return json
     */
    protected String json(Object o) {

        return objectMapper.writeValueAsString(o);
    }

    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithScope(String scope) {

        return jwt().jwt(jwt -> jwt.claims(claims -> claims.put("scope", scope)));
    }

}
