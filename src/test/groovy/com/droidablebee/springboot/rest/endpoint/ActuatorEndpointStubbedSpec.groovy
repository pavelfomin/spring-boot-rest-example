package com.droidablebee.springboot.rest.endpoint

import org.spockframework.spring.SpringSpy
import org.springframework.util.LinkedMultiValueMap
import spock.lang.Ignore

import static com.droidablebee.springboot.rest.endpoint.CustomActuatorEndpoint.CUSTOM
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.isA
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// todo: the beans are getting replaced but not getting registered as custom actuator components
@Ignore("Need to figure out why Spock does not inject Spy beans properly in this case")
class ActuatorEndpointStubbedSpec extends BaseEndpointSpec {

    @SpringSpy
    CustomActuatorEndpoint customActuatorEndpoint

    @SpringSpy
    InfoWebEndpointExtension infoWebEndpointExtension

    LinkedMultiValueMap<String, Number> custom = new LinkedMultiValueMap<>()

    def setup() {
        custom.add('custom1', 11)
        custom.add('custom1', 12)
        custom.add('custom2', 21)
    }

    def getCustomAuthorized() throws Exception {

        when:
        mockMvc.perform(get('/actuator/' + CUSTOM).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.custom1', is(custom.get('custom1'))))
                .andExpect(jsonPath('$.custom2', is(custom.get('custom2'))))

        then:
        1 * customActuatorEndpoint.createCustomMap() >> custom
    }

    def getInfoExtended() throws Exception {


        when:
        mockMvc.perform(get('/actuator/info'))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.build', isA(Object.class)))
                .andExpect(jsonPath('$.build.version', isA(String.class)))
                .andExpect(jsonPath('$.build.artifact', is('spring-boot-rest-example')))
                .andExpect(jsonPath('$.build.group', is('com.droidablebee')))
                .andExpect(jsonPath('$.build.time', isA(Number.class)))
                .andExpect(jsonPath('$.custom1', is(custom.get('custom1'))))
                .andExpect(jsonPath('$.custom2', is(custom.get('custom2'))))

        then:
        1 * infoWebEndpointExtension.createCustomMap() >> custom
    }

}
