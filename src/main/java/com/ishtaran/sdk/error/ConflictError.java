package com.ishtaran.sdk.error;

/** 409, qualquer {@code code} de conflito exceto IDEMPOTENCY_KEY_CONFLICT (ver {@link IdempotencyConflictError}). */
public class ConflictError extends IshtaranError {
    public ConflictError(String message, String code, String requestId, Object details) {
        super(message, 409, code, requestId, details, false);
    }
}
