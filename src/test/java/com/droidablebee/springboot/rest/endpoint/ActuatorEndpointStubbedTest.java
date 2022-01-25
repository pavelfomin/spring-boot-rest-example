package com.droidablebee.springboot.rest.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.util.LinkedMultiValueMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ActuatorEndpointStubbedTest extends BaseEndpointTest {

    @SpyBean
    private CustomActuatorEndpoint customActuatorEndpoint;

    @SpyBean
    private InfoWebEndpointExtension infoWebEndpointExtension;

    @Test
    public void getCustomAuthorized() throws Exception {

        LinkedMultiValueMap<String, Number> custom = new LinkedMultiValueMap<>();
        custom.add("custom1", 11);
        custom.add("custom1", 12);
        custom.add("custom2", 21);

        when(customActuatorEndpoint.createCustomMap()).thenReturn(custom);

        mockMvc.perform(get("/actuator/" + CustomActuatorEndpoint.CUSTOM).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.custom1", is(custom.get("custom1"))))
                .andExpect(jsonPath("$.custom2", is(custom.get("custom2"))))
        ;
    }

    @Test
    public void getInfoExtended() throws Exception {

        LinkedMultiValueMap<String, Number> custom = new LinkedMultiValueMap<>();
        custom.add("custom1", 11);
        custom.add("custom1", 12);
        custom.add("custom2", 21);

        when(infoWebEndpointExtension.createCustomMap()).thenReturn(custom);

        mockMvc.perform(get("/actuator/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.build", isA(Object.class)))
                .andExpect(jsonPath("$.build.version", isA(String.class)))
                .andExpect(jsonPath("$.build.artifact", is("spring-boot-rest-example")))
                .andExpect(jsonPath("$.build.group", is("com.droidablebee")))
                .andExpect(jsonPath("$.build.time", isA(Number.class)))
                .andExpect(jsonPath("$.custom1", is(custom.get("custom1"))))
                .andExpect(jsonPath("$.custom2", is(custom.get("custom2"))))
        ;
    }

}
