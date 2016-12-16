package com.droidablebee.springboot.rest.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseEndpoint.class);

	protected static final String INTERNAL_SERVER_ERROR_MESSAGE = "Failed to process the request"; 
	
	@Autowired
	protected MessageSource messageSource;

	
	@ExceptionHandler
	protected ResponseEntity<?> handleBindException(BindException exception) {
		return ResponseEntity.badRequest().body(convert(exception.getAllErrors()));
	}
	
  	/**
	 * Exception handler for validation errors caused by method parameters @RequesParam, @PathVariable, @RequestHeader annotated with javax.validation constraints.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleConstrainException(ConstraintViolationException exception) {
		
		List<Error> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			String value = (violation.getInvalidValue() == null ? null : violation.getInvalidValue().toString());
			errors.add(new Error(violation.getPropertyPath().toString(), value, violation.getMessage()));
		}
		
		return ResponseEntity.badRequest().body(errors);
	}

	/**
	 * Exception handler for @RequestBody validation errors.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		
		return ResponseEntity.badRequest().body(convert(exception.getBindingResult().getAllErrors()));
	}

  	/**
	 * Exception handler for missing required parameters errors.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleServletRequestBindingException(ServletRequestBindingException exception) {

		return ResponseEntity.badRequest().body(new Error(null, null, exception.getMessage()));
	}

	/**
	 * Exception handler for other errors.
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleException(Throwable exception) {
		
		logger.error(INTERNAL_SERVER_ERROR_MESSAGE, exception);
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(null, null, INTERNAL_SERVER_ERROR_MESSAGE));
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
