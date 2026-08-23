package com.ishtaran.sdk.http;

import com.ishtaran.sdk.config.RetryPolicy;
import com.ishtaran.sdk.error.IshtaranError;
import com.ishtaran.sdk.error.NetworkError;
import com.ishtaran.sdk.error.TimeoutError;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Retry decorator — see SDK_CAPABILITY_SPEC.md §8. Only retries: connection/timeout failure
 * (always), HTTP 429 (always, honoring {@code Retry-After} when present), HTTP 5xx (only if
 * {@link HttpRequest#idempotent()}). Never retries 400/401/403/404/409/422 — those are deterministic.
 */
public final class RetryingTransport implements HttpTransport {

    private final HttpTransport delegate;
    private final RetryPolicy policy;

    public RetryingTransport(HttpTransport delegate, RetryPolicy policy) {
        this.delegate = delegate;
        this.policy = policy;
    }

    @Override
    public HttpResponse send(HttpRequest request) {
        int attempt = 0;
        while (true) {
            try {
                var response = delegate.send(request);
                if (shouldRetryStatus(response.status(), request.idempotent()) && attempt < policy.maxRetries()) {
                    sleepBeforeRetry(attempt, response.header("Retry-After"));
                    attempt++;
                    continue;
                }
                return response;
            } catch (NetworkError | TimeoutError transportFailure) {
                if (attempt < policy.maxRetries()) {
                    sleepBeforeRetry(attempt, null);
                    attempt++;
                    continue;
                }
                throw transportFailure;
            }
        }
    }

    private boolean shouldRetryStatus(int status, boolean idempotent) {
        if (status == 429) {
            return true;
        }
        return status >= 500 && status < 600 && idempotent;
    }

    private void sleepBeforeRetry(int attempt, String retryAfterHeader) {
        long delayMs = retryAfterHeader != null ? parseRetryAfterMs(retryAfterHeader) : backoffMs(attempt);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IshtaranError("Retry interrupted", null, null, null, null, false);
        }
    }

    private long backoffMs(int attempt) {
        double raw = policy.baseBackoff().toMillis() * Math.pow(policy.backoffMultiplier(), attempt);
        long capped = Math.min((long) raw, policy.maxBackoff().toMillis());
        long jitter = ThreadLocalRandom.current().nextLong(0, Math.max(1, capped / 4));
        return capped + jitter;
    }

    private long parseRetryAfterMs(String header) {
        try {
            return Long.parseLong(header.trim()) * 1000L;
        } catch (NumberFormatException e) {
            return backoffMs(0);
        }
    }
}
