package com.droidablebee.springboot.rest.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt

/**
 * AbstractEndpointTest with common test methods.
 */
@SpringBootTest
@AutoConfigureMockMvc
//this creates MockMvc instance correctly, including wiring of the spring security
abstract class BaseEndpointSpec extends Specification {
    static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype())

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    MockMvc mockMvc

    @SpringBean
    JwtDecoder jwtDecoder = Mock()

    Jwt jwt = Mock()


    def setup() {

        jwtDecoder.decode("invalid") >> { String token -> throw new BadJwtException("Mocked token '$token' is invalid") }
        jwtDecoder.decode(_ as String) >> jwt
    }

    /**
     * Returns json representation of the object.
     * @param o instance
     * @return json
     * @throws IOException
     */
    protected String json(Object o) throws IOException {

        return objectMapper.writeValueAsString(o)
    }

    protected SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtWithScope(String scope) {

        return jwt().jwt(jwt -> jwt.claims(claims -> claims.put("scope", scope)))
    }

}
