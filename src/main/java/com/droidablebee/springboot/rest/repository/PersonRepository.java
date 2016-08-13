package com.droidablebee.springboot.rest.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.droidablebee.springboot.rest.domain.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

}