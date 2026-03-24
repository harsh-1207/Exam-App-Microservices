package com.harshbisht.ExamService.exception;

// Thrown when a resource (exam, question, subject) is not found.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}