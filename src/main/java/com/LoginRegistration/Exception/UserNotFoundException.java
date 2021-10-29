package com.LoginRegistration.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UserNotFoundException extends Exception {

	public UserNotFoundException() {

	}

	public UserNotFoundException(String message) {
		super(message);
	}

}