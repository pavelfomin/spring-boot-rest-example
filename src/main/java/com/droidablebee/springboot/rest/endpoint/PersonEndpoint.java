package com.droidablebee.springboot.rest.endpoint;

import com.droidablebee.springboot.rest.domain.Person;
import com.droidablebee.springboot.rest.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@Validated //required for @Valid on method parameters such as @RequesParam, @PathVariable, @RequestHeader
public class PersonEndpoint extends BaseEndpoint {

    static final int DEFAULT_PAGE_SIZE = 10;
    static final String HEADER_TOKEN = "token";
    static final String HEADER_USER_ID = "userId";

    static final String PERSON_READ_PERMISSION = "person-read";
    static final String PERSON_WRITE_PERMISSION = "person-write";

    @Autowired
    private PersonService personService;

    @PreAuthorize("hasAuthority('SCOPE_" + PERSON_READ_PERMISSION + "')")
    @RequestMapping(path = "/v1/persons", method = RequestMethod.GET)
    public Page<Person> getAll(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page) {
        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
        }
        if (page == null) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, size);
        return personService.findAll(pageable);
    }

    @PreAuthorize("hasAuthority('SCOPE_" + PERSON_READ_PERMISSION + "') or @authorizationConfiguration.isDisabled()")
    @RequestMapping(path = "/v1/person/{id}", method = RequestMethod.GET)
    public ResponseEntity<Person> get(@PathVariable("id") Long id) {
        Person person = personService.findOne(id);
        return (person == null ? ResponseEntity.status(HttpStatus.NOT_FOUND) : ResponseEntity.ok()).body(person);
    }

    @InitBinder("person")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new PersonValidator());
    }
}
