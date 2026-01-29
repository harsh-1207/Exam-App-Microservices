package com.harshbisht.AuthService.exception;

public class RegistrationNotAllowedException extends RuntimeException{
    public RegistrationNotAllowedException(String message) {
        super(message);
    }
}
