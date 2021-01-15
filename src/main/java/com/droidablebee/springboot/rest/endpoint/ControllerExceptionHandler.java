package com.droidablebee.springboot.rest.endpoint;

import com.droidablebee.springboot.rest.endpoint.error.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler /*extends ResponseEntityExceptionHandler*/ {
	
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
	protected ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception) {
		
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
	 * Exception handler for invalid payload (e.g. json invalid format error).
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {

		return ResponseEntity.badRequest().body(new Error(null, null, exception.getMessage()));
	}

	@ExceptionHandler
	protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException exception) {
		return new ResponseEntity<Error>(new Error(null, null, exception.getMessage()), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	protected ResponseEntity<?> handleException(Exception exception) {
		return new ResponseEntity<Error>(new Error(null, null, exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
