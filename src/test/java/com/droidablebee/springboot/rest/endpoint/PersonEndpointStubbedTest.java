package com.droidablebee.springboot.rest.endpoint;

import com.droidablebee.springboot.rest.domain.Person;
import com.droidablebee.springboot.rest.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static com.droidablebee.springboot.rest.endpoint.PersonEndpoint.PERSON_READ_PERMISSION;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class PersonEndpointStubbedTest extends BaseEndpointTest {

	@MockBean
	private PersonService personService;

	private Person testPerson;

    @BeforeEach
    public void setup() throws Exception {

    	testPerson = new Person(1L, "Jack", "Bauer");
		when(personService.findOne(1L)).thenReturn(testPerson);
    }

    @Test
    public void getPersonById() throws Exception {

    	mockMvc.perform(get("/v1/person/{id}", 1).with(jwtWithScope(PERSON_READ_PERMISSION)))
    	.andDo(print())
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.id", is(testPerson.getId().intValue())))
    	.andExpect(jsonPath("$.firstName", is(testPerson.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(testPerson.getLastName())))
    	;
    }

    @Test
    public void handleGenericException() throws Exception {

        String message = "Failed to get person by id";
        when(personService.findOne(1L)).thenThrow(new RuntimeException(message));

        mockMvc.perform(get("/v1/person/{id}", 1).with(jwtWithScope(PERSON_READ_PERMISSION)))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.field", nullValue()))
                .andExpect(jsonPath("$.value", nullValue()))
                .andExpect(jsonPath("$.message", is(message)))
        ;
    }
}
