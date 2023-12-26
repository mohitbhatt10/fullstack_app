package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TodoExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException (Exception ex){
		
		return new ResponseEntity<>(
                new CustomizedResponseWithMessage(ex.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	

}
