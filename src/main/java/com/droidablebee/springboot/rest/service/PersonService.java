package com.droidablebee.springboot.rest.service;

import com.droidablebee.springboot.rest.domain.Person;
import com.droidablebee.springboot.rest.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PersonService {

    @Autowired
    private PersonRepository repository;

    /**
     * Returns paginated list of Person instances.
     *
     * @param pageable pageable
     * @return paginated list of Person instances
     */
    @Transactional(readOnly = true)
    public Page<Person> findAll(Pageable pageable) {

        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Person findOne(Long id) {

        Optional<Person> person = repository.findById(id);
        return person.orElse(null);
    }

    public Person save(Person person) {

        return repository.saveAndFlush(person);
    }
}
