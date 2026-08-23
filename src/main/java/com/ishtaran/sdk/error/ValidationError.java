package com.ishtaran.sdk.error;

/**
 * 400, {@code code=VALIDATION_ERROR}. {@code message()}/{@code details()} carry ONE string with
 * all errors joined by "; " — the real API (FluentValidation) does not expose a per-field array (see
 * SDK_CAPABILITY_SPEC.md §6.1/§12.2). This SDK never fakes a per-field structure that does not exist.
 */
public final class ValidationError extends IshtaranError {
    public ValidationError(String message, String requestId, Object details) {
        super(message, 400, "VALIDATION_ERROR", requestId, details, false);
    }
}
