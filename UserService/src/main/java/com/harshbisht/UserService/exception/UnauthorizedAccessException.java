package com.harshbisht.UserService.exception;

// FIX: Renamed from AccessDeniedException to avoid shadowing
// org.springframework.security.access.AccessDeniedException, which Spring Security
// uses internally. Name collisions cause confusing behaviour — Spring's exception
// handlers catch the wrong type, bypassing your GlobalExceptionHandler.
public class UnauthorizedAccessException extends RuntimeException {
  public UnauthorizedAccessException(String message) {
    super(message);
  }
}