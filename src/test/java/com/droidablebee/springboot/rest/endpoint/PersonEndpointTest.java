package com.droidablebee.springboot.rest.endpoint;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.droidablebee.springboot.rest.domain.Person;
import com.droidablebee.springboot.rest.service.PersonService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class PersonEndpointTest extends BaseEndpointTest {

	@Autowired
	private PersonService personService;
	
	private Person testPerson;
	private long timestamp;
	
    @Before
    public void setup() throws Exception {
    	super.setup();

    	timestamp = new Date().getTime();

    	// create test persons
    	personService.save(createPerson("Jack", "Bauer"));
    	personService.save(createPerson("Chloe", "O'Brian"));
    	personService.save(createPerson("Kim", "Bauer"));
    	personService.save(createPerson("David", "Palmer"));
    	personService.save(createPerson("Michelle", "Dessler"));

    	Page<Person> persons = personService.findAll(new PageRequest(0, PersonEndpoint.DEFAULT_PAGE_SIZE));
		assertNotNull(persons);
		assertEquals(5L, persons.getTotalElements());
		
		testPerson = persons.getContent().get(0);
    }

    @Test
    public void getPersonById() throws Exception {
    	Long id = testPerson.getId();
    	
    	MvcResult result = mockMvc.perform(get("/v1/person/{id}", id))
    	.andExpect(status().isOk())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.id", is(id.intValue())))
    	.andExpect(jsonPath("$.firstName", is(testPerson.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(testPerson.getLastName())))
    	.andExpect(jsonPath("$.dateOfBirth", isA(String.class)))
    	.andReturn()
    	;
    	
    	logger.debug("content="+ result.getResponse().getContentAsString());
    }

	private Person createPerson(String first, String last) {
		Person person = new Person(first, last);
		person.setDateOfBirth(new Date(timestamp));
		return person;
	}

}
