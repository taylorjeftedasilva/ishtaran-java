package com.ishtaran.sdk.error;

/**
 * Base of every exception thrown by the SDK — see SDK_CAPABILITY_SPEC.md §6.4. {@code httpStatus}/
 * {@code code} are null for {@link NetworkError}/{@link TimeoutError} (no HTTP response ever
 * existed); {@code code}/{@code details} are always null for {@link AuthenticationError}/
 * {@link AuthorizationError} (401/403 never have a body — see §6.3, Known Gap §12.1).
 */
public class IshtaranError extends RuntimeException {

    private final Integer httpStatus;
    private final String code;
    private final String requestId;
    private final Object details;
    private final boolean retryable;

    public IshtaranError(String message, Integer httpStatus, String code, String requestId,
                          Object details, boolean retryable) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.requestId = requestId;
        this.details = details;
        this.retryable = retryable;
    }

    public Integer httpStatus() {
        return httpStatus;
    }

    /** Stable domain error key (e.g. {@code VALIDATION_ERROR}) — null when not applicable. */
    public String code() {
        return code;
    }

    /**
     * Always null today — the real API does not implement any request/correlation ID mechanism
     * (exhaustive search in src/CompositionRoot/, zero occurrences — see SDK_CAPABILITY_SPEC.md
     * §12.1). Field kept for when that mechanism exists in the backend, without a breaking change.
     */
    public String requestId() {
        return requestId;
    }

    public Object details() {
        return details;
    }

    public boolean retryable() {
        return retryable;
    }
}
