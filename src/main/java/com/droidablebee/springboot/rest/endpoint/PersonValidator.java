package com.droidablebee.springboot.rest.endpoint;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import com.droidablebee.springboot.rest.domain.Person;

public class PersonValidator 
//implements Validator { 
extends CustomValidatorBean {

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        
    	super.validate(target, errors);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "middleName", "field.required");
        System.out.println("PersonValidator.validate() target="+ target +" errors="+ errors);
    }

}