package com.ishtaran.sdk.error;

/** Fallback — any 4xx/5xx whose {@code code} is not recognized by {@link ErrorMapper}'s map. */
public final class ApiError extends IshtaranError {
    public ApiError(String message, int httpStatus, String code, String requestId, Object details, boolean retryable) {
        super(message, httpStatus, code, requestId, details, retryable);
    }
}
