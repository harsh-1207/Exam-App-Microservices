package com.harshbisht.AuthService.service;

import com.harshbisht.AuthService.exception.AccountLockedException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SECURITY FIX: /auth/login previously had no protection against credential
 * stuffing or brute-force password guessing — any number of attempts could
 * be made against any email with no penalty.
 *
 * This is a minimal, dependency-free in-memory limiter: 5 failed attempts
 * for a given email locks that email out for 15 minutes. It resets on a
 * successful login.
 *
 * NOTE: in-memory means the counters reset if the instance restarts, and
 * don't share state across multiple AuthService instances behind a load
 * balancer. For a real multi-instance deployment, back this with Redis
 * (or similar shared store) instead of a ConcurrentHashMap. Fine as a
 * baseline for a single-instance setup.
 */
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    private static class Attempt {
        int failureCount = 0;
        Instant lockedUntil = null;
    }

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
}