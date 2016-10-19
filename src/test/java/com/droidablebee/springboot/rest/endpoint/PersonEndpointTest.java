package com.droidablebee.springboot.rest.endpoint;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.EntityManager;

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
	EntityManager entityManager;
	
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
		
		//refresh entity with any changes that have been done during persistence including Hibernate conversion
		//example: java.util.Date field is injected with either with java.sql.Date (if @Temporal(TemporalType.DATE) is used)
		//or java.sql.Timestamp
		entityManager.refresh(testPerson);
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

    /**
     * Test JSR-303 bean validation. 
     */
    @Test
    public void createPersonValidationErrorLastName() throws Exception {
    	
    	//person with missing last name
    	Person person = createPerson("first", null);
    	person.setMiddleName("middle");
    	String content = json(person);
		mockMvc.perform(
				put("/v1/person")
				.accept(JSON_MEDIA_TYPE)
				.content(content)
				.contentType(JSON_MEDIA_TYPE))
		.andExpect(status().isBadRequest())
//		.andExpect(content().contentType(JSON_MEDIA_TYPE))
//		.andExpect(jsonPath("$.exception", is("org.springframework.web.bind.MethodArgumentNotValidException")))
		;
    }

    /**
     * Test custom bean validation. 
     */
    @Test
    public void createPersonValidationErrorMiddleName() throws Exception {
    	
    	//person with missing middle name - custom validation
    	Person person = createPerson("first", "last");
    	String content = json(person);
		mockMvc.perform(
				put("/v1/person")
				.accept(JSON_MEDIA_TYPE)
				.content(content)
				.contentType(JSON_MEDIA_TYPE))
		.andExpect(status().isBadRequest())
//		.andExpect(content().contentType(JSON_MEDIA_TYPE))
//		.andExpect(jsonPath("$.exception", is("org.springframework.web.bind.MethodArgumentNotValidException")))
		;
    }

    @Test
    public void createPerson() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middleName");

    	String content = json(person);
		
    	mockMvc.perform(
				put("/v1/person")
				.accept(JSON_MEDIA_TYPE)
				.content(content)
				.contentType(JSON_MEDIA_TYPE))
		.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id", isA(Number.class)))
    	.andExpect(jsonPath("$.firstName", is(person.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(person.getLastName())))
    	.andExpect(jsonPath("$.dateOfBirth", isA(String.class)))
    	.andExpect(jsonPath("$.dateOfBirth", is(person.getDateOfBirth().toString())))
		;
    }

    @Test
    public void createPersonWithDateVerification() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middleName");
    	
    	String content = json(person);
    	
    	mockMvc.perform(
    			put("/v1/person")
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id", isA(Number.class)))
    	.andExpect(jsonPath("$.firstName", is(person.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(person.getLastName())))
    	.andExpect(jsonPath("$.dateOfBirth", isA(String.class)))
    	.andExpect(jsonPath("$.dateOfBirth", is(person.getDateOfBirth().toString())))
    	;
    	
    }

	private Person createPerson(String first, String last) {
		Person person = new Person(first, last);
		person.setDateOfBirth(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate());
		return person;
	}

}
