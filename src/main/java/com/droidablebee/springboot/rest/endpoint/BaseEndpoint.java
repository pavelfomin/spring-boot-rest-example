package com.droidablebee.springboot.rest.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseEndpoint {

	@Autowired
	protected MessageSource messageSource; 
	
	@ExceptionHandler
	protected ResponseEntity<?> handleBindException(BindException exception) {
		return ResponseEntity.badRequest().body(convert(exception.getAllErrors()));
	}
	
	@ExceptionHandler
	protected ResponseEntity<?> handleConstrainException(ConstraintViolationException exception) {
		
		List<Error> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			String value = (violation.getInvalidValue() == null ? null : violation.getInvalidValue().toString());
			errors.add(new Error(violation.getPropertyPath().toString(), value, violation.getMessage()));
		}
		
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler
	protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		return ResponseEntity.badRequest().body(convert(exception.getBindingResult().getAllErrors()));
	}

	protected List<Error> convert(List<ObjectError> objectErrors) {
		
		List<Error> errors = new ArrayList<>();
		
		for (ObjectError objectError : objectErrors) {

			String message = objectError.getDefaultMessage(); 
			if (message == null) {
				//when using custom spring validator org.springframework.validation.Validator need to resolve messages manually 
				message = messageSource.getMessage(objectError, null);
			}
			
			Error error = null;
			if (objectError instanceof FieldError) {
				FieldError fieldError = (FieldError) objectError;
				String value = (fieldError.getRejectedValue() == null ? null : fieldError.getRejectedValue().toString());
				error = new Error(fieldError.getField(), value, message);
			} else {
				error = new Error(objectError.getObjectName(), objectError.getCode(), objectError.getDefaultMessage());
			}
			
			errors.add(error);
		}
		
		return errors;
	}

}
