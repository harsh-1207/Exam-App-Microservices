package com.harshbisht.AuthService.service;

import com.harshbisht.AuthService.exception.AccountLockedException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private final ConcurrentMap<String, Attempt> attemptsByEmail = new ConcurrentHashMap<>();

    /** Call before checking credentials. Throws if this email is currently locked out. */
    public void assertNotLocked(String email) {
        Attempt attempt = attemptsByEmail.get(normalize(email));
        if (attempt != null && attempt.lockedUntil != null && Instant.now().isBefore(attempt.lockedUntil)) {
            throw new AccountLockedException(
                    "Too many failed login attempts. Please try again later.");
        }
    }

    /** Call after a failed login (wrong email OR wrong password — caller doesn't distinguish). */
    public void recordFailure(String email) {
        attemptsByEmail.compute(normalize(email), (key, attempt) -> {
            if (attempt == null) {
                attempt = new Attempt();
            }
            // Lockout window expired — start counting fresh.
            if (attempt.lockedUntil != null && Instant.now().isAfter(attempt.lockedUntil)) {
                attempt.failureCount = 0;
                attempt.lockedUntil = null;
            }
            attempt.failureCount++;
            if (attempt.failureCount >= MAX_ATTEMPTS) {
                attempt.lockedUntil = Instant.now().plus(LOCKOUT_DURATION);
            }
            return attempt;
        });
    }

    /** Call after a successful login to clear any prior failure history. */
    public void recordSuccess(String email) {
        attemptsByEmail.remove(normalize(email));
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static class Attempt {
        int failureCount = 0;
        Instant lockedUntil = null;
    }
}