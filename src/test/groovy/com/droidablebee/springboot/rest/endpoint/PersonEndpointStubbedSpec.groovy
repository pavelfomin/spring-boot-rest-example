package com.droidablebee.springboot.rest.endpoint

import com.droidablebee.springboot.rest.domain.Person
import com.droidablebee.springboot.rest.service.PersonService
import org.spockframework.spring.SpringBean

import static com.droidablebee.springboot.rest.endpoint.PersonEndpoint.PERSON_READ_PERMISSION
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.nullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PersonEndpointStubbedSpec extends BaseEndpointSpec {

    @SpringBean
    PersonService personService = Mock()

    Person testPerson

    def setup() {

        testPerson = new Person(1L, 'Jack', 'Bauer')
        personService.findOne(1L) >> testPerson
        jwt.hasClaim('scope') >> true
        jwt.getClaim('scope') >> [PERSON_READ_PERMISSION]
    }

    def getPersonById() throws Exception {

        expect:
        mockMvc.perform(get('/v1/person/{id}', 1)
                .header('Authorization', 'Bearer valid')
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.id', is(testPerson.getId().intValue())))
                .andExpect(jsonPath('$.firstName', is(testPerson.getFirstName())))
                .andExpect(jsonPath('$.lastName', is(testPerson.getLastName())))
    }

    def handleGenericException() throws Exception {

        String message = 'Failed to get person by id'

        when:
        mockMvc.perform(get('/v1/person/{id}', 1)
                .header('Authorization', 'Bearer valid')
        )
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.field', nullValue()))
                .andExpect(jsonPath('$.value', nullValue()))
                .andExpect(jsonPath('$.message', is(message)))

        then:
        1 * personService.findOne(1L) >> { throw new RuntimeException(message) }
    }
}
