package com.ishtaran.sdk.error;

/** 429, {@code code=RATE_LIMITED}. Always retryable — exposes {@code retryAfterSeconds} from the real header. */
public final class RateLimitError extends IshtaranError {

    private final Integer retryAfterSeconds;

    public RateLimitError(String message, String requestId, Object details, Integer retryAfterSeconds) {
        super(message, 429, "RATE_LIMITED", requestId, details, true);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Integer retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
