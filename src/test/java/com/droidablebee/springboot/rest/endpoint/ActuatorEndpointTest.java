package com.droidablebee.springboot.rest.endpoint;

import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ActuatorEndpointTest extends BaseEndpointTest {

    @Test
    public void getInfo() throws Exception {
    	
    	mockMvc.perform(get("/actuator/info"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.build", isA(Object.class)))
				.andExpect(jsonPath("$.build.version", isA(String.class)))
				.andExpect(jsonPath("$.build.artifact", is("spring-boot-rest-example")))
				.andExpect(jsonPath("$.build.group", is("com.droidablebee")))
		    	.andExpect(jsonPath("$.build.time", isA(Number.class)))
    	;
    }

	@Test
	public void getHealth() throws Exception {

		mockMvc.perform(get("/actuator/health"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.components").doesNotExist())
		;
	}

	@Test
	public void getHealthAuthorized() throws Exception {

		mockMvc.perform(get("/actuator/health").with(jwt()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.components").doesNotExist())
		;
	}

	@Test
	public void getHealthAuthorizedWithConfiguredRole() throws Exception {

		mockMvc.perform(get("/actuator/health").with(jwtWithScope("health-details")))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.status", is("UP")))
				.andExpect(jsonPath("$.components", isA(Object.class)))
				.andExpect(jsonPath("$.components.diskSpace.status", is("UP")))
				.andExpect(jsonPath("$.components.diskSpace.details", isA(Object.class)))
				.andExpect(jsonPath("$.components.db.status", is("UP")))
				.andExpect(jsonPath("$.components.db.details", isA(Object.class)))
				.andExpect(jsonPath("$.components.ping.status", is("UP")))
		;
	}

	@Test
	public void getEnv() throws Exception {

		mockMvc.perform(get("/actuator/env"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				;
	}

	@Test
	public void getEnvAuthorized() throws Exception {

		mockMvc.perform(get("/actuator/env").with(jwt()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(jsonPath("$.activeProfiles", isA(JSONArray.class)))
				.andExpect(jsonPath("$.propertySources", isA(JSONArray.class)))
				;
	}

	@Test
	public void getCustom() throws Exception {

		mockMvc.perform(get("/actuator/"+ CustomActuatorEndpoint.CUSTOM))
				.andDo(print())
				.andExpect(status().isUnauthorized())
		;
	}

	@Test
	public void getCustomAuthorized() throws Exception {

		mockMvc.perform(get("/actuator/"+ CustomActuatorEndpoint.CUSTOM).with(jwt()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(JSON_MEDIA_TYPE))
				.andExpect(content().string("{}"))
		;
	}

}
