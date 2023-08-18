package com.droidablebee.springboot.rest.repository;

import com.droidablebee.springboot.rest.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void getOne() {

        Person person = personRepository.getOne(Long.MAX_VALUE);
        assertNotNull(person);
        //access to the Entity's reference state should cause jakarta.persistence.EntityNotFoundException
        assertNotNull(person.getId()); // accessing id won't throw an exception
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> person.getFirstName());
    }

    @Test
    public void getReferenceUsingEntityManager() {

        Person person = entityManager.getReference(Person.class, Long.MAX_VALUE);
        assertNotNull(person);
        //access to the Entity's reference state should cause jakarta.persistence.EntityNotFoundException
        assertNotNull(person.getId()); // accessing id won't throw an exception
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> person.getFirstName());
    }

    @Test
    public void findByIdUsingOptional() {

        Optional<Person> optional = personRepository.findById(Long.MAX_VALUE);
        assertNotNull(optional);
        assertFalse(optional.isPresent());
        assertThrows(java.util.NoSuchElementException.class, () -> optional.get());
    }

    @Test
    public void findByIdUsingEntityManager() {

        Person person = entityManager.find(Person.class, Long.MAX_VALUE);
        assertNull(person);
    }

}