package com.harshbisht.AuthService.exception;

// Thrown by LoginAttemptService when an email has exceeded the failed-login
// threshold and is temporarily locked out.
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}