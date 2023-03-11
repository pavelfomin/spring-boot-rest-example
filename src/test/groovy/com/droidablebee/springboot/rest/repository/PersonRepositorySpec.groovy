package com.droidablebee.springboot.rest.repository

import com.droidablebee.springboot.rest.domain.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext

@DataJpaTest
/*
By default @DataJpaTest uses embeded h2 databaze and ignores the connection string declared in application.properties.
Annotation @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) disables this behavior.
*/
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositorySpec extends Specification {

    @Autowired
    PersonRepository personRepository

    @PersistenceContext
    EntityManager entityManager

    def getOne() {

        when:
        Person person = personRepository.getOne(Long.MAX_VALUE)

        then:
        person

        and: "accessing id won't throw an exception"
        person.getId()

        when: "accessing the Entity's reference state should cause javax.persistence.EntityNotFoundException"
        person.getFirstName()

        then:
        thrown(EntityNotFoundException)
    }

    def getReferenceUsingEntityManager() {

        when:
        Person person = entityManager.getReference(Person.class, Long.MAX_VALUE)

        then:
        person

        and: "accessing id won't throw an exception"
        person.getId()

        when: "accessing the Entity's reference state should cause javax.persistence.EntityNotFoundException"
        person.getFirstName()

        then:
        thrown(EntityNotFoundException)
    }

    def findByIdUsingOptional() {

        when:
        Optional<Person> optional = personRepository.findById(Long.MAX_VALUE)

        then:
        optional != null
        !optional
        optional.empty
    }

    def findByIdUsingEntityManager() {

        when:
        Person person = entityManager.find(Person.class, Long.MAX_VALUE)

        then:
        !person
    }

}