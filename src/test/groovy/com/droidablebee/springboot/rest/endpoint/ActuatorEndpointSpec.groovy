package com.droidablebee.springboot.rest.endpoint

import net.minidev.json.JSONArray

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.isA
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ActuatorEndpointSpec extends BaseEndpointSpec {

    def getInfo() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/info'))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.build', isA(Object.class)))
                .andExpect(jsonPath('$.build.version', isA(String.class)))
                .andExpect(jsonPath('$.build.artifact', is('spring-boot-rest-example')))
                .andExpect(jsonPath('$.build.group', is('com.droidablebee')))
                .andExpect(jsonPath('$.build.time', isA(Number.class)))

    }

    def getHealth() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/health'))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.status', is('UP')))
                .andExpect(jsonPath('$.components').doesNotExist())

    }

    def getHealthAuthorized() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/health').with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.status', is('UP')))
                .andExpect(jsonPath('$.components').doesNotExist())

    }

    def getHealthAuthorizedWithConfiguredRole() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/health').with(jwtWithScope('health-details')))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.status', is('UP')))
                .andExpect(jsonPath('$.components', isA(Object.class)))
                .andExpect(jsonPath('$.components.diskSpace.status', is('UP')))
                .andExpect(jsonPath('$.components.diskSpace.details', isA(Object.class)))
                .andExpect(jsonPath('$.components.db.status', is('UP')))
                .andExpect(jsonPath('$.components.db.details', isA(Object.class)))
                .andExpect(jsonPath('$.components.ping.status', is('UP')))

    }

    def getEnv() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/env'))
                .andDo(print())
                .andExpect(status().isUnauthorized())

    }

    def getEnvAuthorized() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/env').with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.activeProfiles', isA(JSONArray.class)))
                .andExpect(jsonPath('$.propertySources', isA(JSONArray.class)))

    }

    def getCustom() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/' + CustomActuatorEndpoint.CUSTOM))
                .andDo(print())
                .andExpect(status().isUnauthorized())

    }

    def getCustomAuthorized() throws Exception {

        expect:
        mockMvc.perform(get('/actuator/' + CustomActuatorEndpoint.CUSTOM).with(jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(content().string('{}'))

    }

}
