package com.harshbisht.ExamService.exception;

// Thrown when a teacher tries to modify an exam they don't own,
// or a student tries to access an unpublished exam.
public class AccessDeniedException extends RuntimeException {
  public AccessDeniedException(String message) {
    super(message);
  }
}