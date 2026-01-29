package com.harshbisht.WebService.exception;

public class PageAccessDeniedException extends RuntimeException {
    public PageAccessDeniedException(String message) { super(message); }
}