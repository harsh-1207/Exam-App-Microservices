package com.harshbisht.AuthService.exception;

import com.harshbisht.AuthService.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // SECURITY FIX: UserNotFoundException / its handler removed. login() no
    // longer distinguishes "no such email" from "wrong password" — both now
    // throw InvalidCredentialsException above, so this class was deleted
    // rather than left dangling and unused.

    @ExceptionHandler(RegistrationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationNotAllowed(RegistrationNotAllowedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserProfileCreationException.class)
    public ResponseEntity<ErrorResponse> handleProfileCreation(UserProfileCreationException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // SECURITY FIX: new — maps LoginAttemptService's lockout to 429 Too Many
    // Requests, the correct status for rate/attempt limiting (RFC 6585).
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    // SECURITY FIX: new — @Valid on RegisterRequest/LoginRequest throws this
    // when validation fails. Without a handler here it still returns 400 via
    // Spring's default handling, but with a different body shape than the
    // rest of this API's ErrorResponse — this keeps the contract consistent.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(message, status.value(), LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}