package com.ishtaran.sdk.error;

/**
 * 409, {@code code=IDEMPOTENCY_KEY_CONFLICT} — the same key resent with a payload different from
 * the original (see SDK_CAPABILITY_SPEC.md §9). Subtype of {@link ConflictError} to allow
 * catching either generically OR specifically.
 */
public final class IdempotencyConflictError extends ConflictError {
    public IdempotencyConflictError(String message, String requestId, Object details) {
        super(message, "IDEMPOTENCY_KEY_CONFLICT", requestId, details);
    }
}
