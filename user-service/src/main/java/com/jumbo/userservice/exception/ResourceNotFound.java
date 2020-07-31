package com.jumbo.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class handles NOT_FOUND replies to the front-end. 
 * 
 * @author André Janino
 */
@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ResourceNotFound(String message) {
		super(message);
	}
	
	public ResourceNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}