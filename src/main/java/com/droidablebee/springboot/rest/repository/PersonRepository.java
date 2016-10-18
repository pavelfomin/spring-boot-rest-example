package com.droidablebee.springboot.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droidablebee.springboot.rest.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}