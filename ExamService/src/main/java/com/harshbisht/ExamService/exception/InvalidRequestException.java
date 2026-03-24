package com.harshbisht.ExamService.exception;

// Thrown on invalid input — e.g. wrong number of options, blank question text.
public class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }
}