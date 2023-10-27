package com.droidablebee.springboot.rest.repository;

import com.droidablebee.springboot.rest.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}