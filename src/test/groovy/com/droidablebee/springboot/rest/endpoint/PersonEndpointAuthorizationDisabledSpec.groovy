package com.droidablebee.springboot.rest.endpoint

import com.droidablebee.springboot.rest.domain.Person
import com.droidablebee.springboot.rest.service.PersonService
import org.spockframework.spring.SpringBean
import org.springframework.test.context.TestPropertySource

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestPropertySource(properties = [
        "app.authorization.enabled:false"
])
class PersonEndpointAuthorizationDisabledSpec extends BaseEndpointSpec {

    @SpringBean
    PersonService personService = Mock()

    Person testPerson

    def setup() {

        testPerson = new Person(1L, 'Jack', 'Bauer')
        personService.findOne(1L) >> testPerson
    }

    def "get person by id does not require scope if authorization is disabled"() throws Exception {
        Long id = testPerson.id

        expect:
        mockMvc.perform(get('/v1/person/{id}', id)
                .header('Authorization', 'Bearer valid')
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath('$.id', is(testPerson.getId().intValue())))
                .andExpect(jsonPath('$.firstName', is(testPerson.getFirstName())))
                .andExpect(jsonPath('$.lastName', is(testPerson.getLastName())))
    }
}
