package com.ishtaran.sdk.config;

import java.time.Duration;

/**
 * Retry policy — see SDK_CAPABILITY_SPEC.md §8. Retries only on connection failure, 429 (honoring
 * {@code Retry-After}), and 5xx when the call carries an Idempotency-Key. Never on deterministic 4xx
 * (400/401/403/404/409/422).
 *
 * @param maxRetries       additional attempts beyond the first (default 2 → up to 3 total attempts)
 * @param baseBackoff      base delay for exponential backoff (default 200ms)
 * @param backoffMultiplier multiplicative factor per attempt (default 2.0)
 * @param maxBackoff       backoff ceiling, before jitter (default 5s)
 */
public record RetryPolicy(int maxRetries, Duration baseBackoff, double backoffMultiplier, Duration maxBackoff) {

    public static RetryPolicy defaults() {
        return new RetryPolicy(2, Duration.ofMillis(200), 2.0, Duration.ofSeconds(5));
    }

    public static RetryPolicy disabled() {
        return new RetryPolicy(0, Duration.ZERO, 1.0, Duration.ZERO);
    }

    public RetryPolicy {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries cannot be negative");
        }
    }
}
