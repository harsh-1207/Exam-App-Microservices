package com.harshbisht.WebService.exception;

public class InvalidSessionTokenException extends RuntimeException {
    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
