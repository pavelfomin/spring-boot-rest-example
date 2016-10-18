package com.droidablebee.springboot.rest.endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.droidablebee.springboot.rest.domain.Person;

/**
 * This test will introduce data source pollution since transaction boundaries won't apply.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class PersonEndpointIntegrationTest extends BaseEndpointTest {

	@Autowired
	private TestRestTemplate template;
	
	@Value("${local.server.port}")
	private int port;
	
	private long timestamp;
	
    @Before
    public void setup() throws Exception {
    	super.setup();

    	timestamp = new Date().getTime();
    }

    @Test
    public void createPerson() throws Exception {

    	Person person = createPerson("first", "last");
    	person.setMiddleName("middleName");

    	ResponseEntity<Person> responseEntity = template.postForEntity("http://localhost:{port}/v1/person", person, Person.class, port);
    	assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    	
    	Person createdPerson = responseEntity.getBody();
    	assertNotNull(createdPerson);
    	assertNotNull(createdPerson.getId());
    	assertEquals(person.getFirstName(), createdPerson.getFirstName());
    	assertEquals(person.getLastName(), createdPerson.getLastName());
    	assertEquals(person.getMiddleName(), createdPerson.getMiddleName());
    	assertEquals(person.getDateOfBirth(), createdPerson.getDateOfBirth());
    	
    	responseEntity = template.getForEntity("http://localhost:{port}/v1/person/{id}", Person.class, port, createdPerson.getId());
    	assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    	
    	Person foundPerson = responseEntity.getBody();
    	assertNotNull(foundPerson);
    	assertEquals(createdPerson.getId(), foundPerson.getId());
    	assertEquals(person.getFirstName(), foundPerson.getFirstName());
    	assertEquals(person.getLastName(), foundPerson.getLastName());
    	assertEquals(person.getMiddleName(), foundPerson.getMiddleName());
    	assertEquals(person.getDateOfBirth(), foundPerson.getDateOfBirth());
    }

	private Person createPerson(String first, String last) {
		Person person = new Person(first, last);
		person.setDateOfBirth(new Date(timestamp));
		return person;
	}

}
