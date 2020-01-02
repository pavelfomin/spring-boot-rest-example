package com.droidablebee.springboot.rest.repository;

import com.droidablebee.springboot.rest.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
/*
By default @DataJpaTest uses embeded h2 databaze and ignores the connection string declared in application.properties.
Annotation @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) disables this behavior.
*/
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test(expected = javax.persistence.EntityNotFoundException.class)
    public void getOne() {

        Person person = personRepository.getOne(Long.MAX_VALUE);
        assertNotNull(person);
        //access to the Entity's reference state should cause javax.persistence.EntityNotFoundException
        assertNotNull(person.getId()); // accessing id won't throw an exception
        person.getFirstName(); // will throw exception
    }

    @Test(expected = javax.persistence.EntityNotFoundException.class)
    public void getReferenceUsingEntityManager() {

        Person person = entityManager.getReference(Person.class, Long.MAX_VALUE);
        assertNotNull(person);
        //access to the Entity's reference state should cause javax.persistence.EntityNotFoundException
        assertNotNull(person.getId()); // accessing id won't throw an exception
        person.getFirstName(); // will throw exception
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void findByIdUsingOptional() {

        Optional<Person> optional = personRepository.findById(Long.MAX_VALUE);
        assertNotNull(optional);
        assertFalse(optional.isPresent());
        optional.get();
    }

    @Test
    public void findByIdUsingEntityManager() {

        Person person = entityManager.find(Person.class, Long.MAX_VALUE);
        assertNull(person);
    }

}