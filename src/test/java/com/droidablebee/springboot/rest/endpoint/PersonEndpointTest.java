package com.droidablebee.springboot.rest.endpoint;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;

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

import com.droidablebee.springboot.rest.domain.Address;
import com.droidablebee.springboot.rest.domain.Person;
import com.droidablebee.springboot.rest.domain.Person.Gender;
import com.droidablebee.springboot.rest.service.PersonService;

import net.minidev.json.JSONArray;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class PersonEndpointTest extends BaseEndpointTest {

	@Autowired
	private EntityManager entityManager;
	
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

    	Page<Person> persons = personService.findAll(PageRequest.of(0, PersonEndpoint.DEFAULT_PAGE_SIZE));
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
    	
    	mockMvc.perform(get("/v1/person/{id}", id))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(JSON_MEDIA_TYPE))
			.andExpect(jsonPath("$.id", is(id.intValue())))
			.andExpect(jsonPath("$.firstName", is(testPerson.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(testPerson.getLastName())))
			.andExpect(jsonPath("$.dateOfBirth", isA(Number.class)))
    	;
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
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(JSON_MEDIA_TYPE))
		.andExpect(jsonPath("$", isA(JSONArray.class)))
		.andExpect(jsonPath("$.length()", is(1)))
    	.andExpect(jsonPath("$.[?(@.field == 'lastName')].message", hasItem("must not be null")))
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
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(JSON_MEDIA_TYPE))
		.andExpect(jsonPath("$", isA(JSONArray.class)))
		.andExpect(jsonPath("$.length()", is(1)))
    	.andExpect(jsonPath("$.[?(@.field == 'middleName')].message", hasItem("middle name is required")))
		;
    }

    /**
     * Test JSR-303 bean object graph validation with nested entities. 
     */
    @Test
    public void createPersonValidationAddress() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middle");
    	person.addAddress(new Address("line1", "city", "state", "zip"));
    	person.addAddress(new Address()); //invalid address

    	String content = json(person);
		mockMvc.perform(
				put("/v1/person")
				.accept(JSON_MEDIA_TYPE)
				.content(content)
				.contentType(JSON_MEDIA_TYPE))
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(JSON_MEDIA_TYPE))
		.andExpect(jsonPath("$.length()", is(4)))
    	.andExpect(jsonPath("$.[?(@.field == 'addresses[].line1')].message", hasItem("must not be null")))
    	.andExpect(jsonPath("$.[?(@.field == 'addresses[].state')].message", hasItem("must not be null")))
    	.andExpect(jsonPath("$.[?(@.field == 'addresses[].city')].message", hasItem("must not be null")))
    	.andExpect(jsonPath("$.[?(@.field == 'addresses[].zip')].message", hasItem("must not be null")))
		;
    }

    @Test
    public void createPersonValidationToken() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middle");
    	
    	String content = json(person);
    	mockMvc.perform(
    			put("/v1/person")
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
    			.header(PersonEndpoint.HEADER_TOKEN, "1") //invalid token
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isBadRequest())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.length()", is(1)))
    	.andExpect(jsonPath("$.[?(@.field == 'add.arg2')].message", hasItem("token size 2-40")))
    	;
    }
    
    @Test
    public void createPersonValidationUserId() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middle");
    	
    	String content = json(person);
    	mockMvc.perform(
    			put("/v1/person")
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isBadRequest())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.message", containsString("Missing request header '"+ PersonEndpoint.HEADER_USER_ID)))
    	;
    }
    
    @Test
    public void createPerson() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middleName");

    	String content = json(person);
		
    	mockMvc.perform(
				put("/v1/person")
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
				.accept(JSON_MEDIA_TYPE)
				.content(content)
				.contentType(JSON_MEDIA_TYPE))
		.andDo(print())
		.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id", isA(Number.class)))
    	.andExpect(jsonPath("$.firstName", is(person.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(person.getLastName())))
    	.andExpect(jsonPath("$.dateOfBirth", isA(Number.class)))
    	.andExpect(jsonPath("$.dateOfBirth", is(person.getDateOfBirth().getTime())))
		;
    }

    @Test
    public void createPersonWithDateVerification() throws Exception {
    	
    	Person person = createPerson("first", "last");
    	person.setMiddleName("middleName");
    	
    	String content = json(person);
    	
    	mockMvc.perform(
    			put("/v1/person")
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id", isA(Number.class)))
    	.andExpect(jsonPath("$.firstName", is(person.getFirstName())))
    	.andExpect(jsonPath("$.lastName", is(person.getLastName())))
    	.andExpect(jsonPath("$.dateOfBirth", isA(Number.class)))
    	.andExpect(jsonPath("$.dateOfBirth", is(person.getDateOfBirth().getTime())))
    	;
    	
    }

    @Test
    public void requestBodyValidationInvalidJsonValue() throws Exception {
    	
    	testPerson.setGender(Gender.M);
    	String content = json(testPerson);
    	//payload with invalid gender
    	content = content.replaceFirst("(\"gender\":\")(M)(\")", "$1Q$3");

    	mockMvc.perform(
    			put("/v1/person")
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isBadRequest())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.message", containsString("Cannot deserialize value of type `com.droidablebee.springboot.rest.domain.Person$Gender`")))
    	;
    }
    
    @Test
    public void requestBodyValidationInvalidJson() throws Exception {
    	
    	String content = json("not valid json");
    	mockMvc.perform(
    			put("/v1/person")
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isBadRequest())
    	.andExpect(content().contentType(JSON_MEDIA_TYPE))
    	.andExpect(jsonPath("$.message", containsString("Cannot construct instance of `com.droidablebee.springboot.rest.domain.Person`")))
    	;
    }

    @Test
    public void handleHttpRequestMethodNotSupportedException() throws Exception {
    	
    	String content = json(testPerson);
    	
    	mockMvc.perform(
    			delete("/v1/person") //not supported method
    			.header(PersonEndpoint.HEADER_USER_ID, UUID.randomUUID())
    			.accept(JSON_MEDIA_TYPE)
    			.content(content)
    			.contentType(JSON_MEDIA_TYPE))
    	.andDo(print())
    	.andExpect(status().isMethodNotAllowed())
    	.andExpect(content().string(""))
    	;
    }

	private Person createPerson(String first, String last) {
		Person person = new Person(first, last);
		person.setDateOfBirth(new Date(timestamp));
		return person;
	}

}
