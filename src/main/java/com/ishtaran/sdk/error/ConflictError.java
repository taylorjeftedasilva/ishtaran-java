package com.ishtaran.sdk.error;

/** 409, any conflict {@code code} except IDEMPOTENCY_KEY_CONFLICT (see {@link IdempotencyConflictError}). */
public class ConflictError extends IshtaranError {
    public ConflictError(String message, String code, String requestId, Object details) {
        super(message, 409, code, requestId, details, false);
    }
}
