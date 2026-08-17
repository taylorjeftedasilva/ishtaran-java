package com.ishtaran.sdk.error;

/** Fallback — qualquer 4xx/5xx cujo {@code code} não é reconhecido pelo mapa de {@link ErrorMapper}. */
public final class ApiError extends IshtaranError {
    public ApiError(String message, int httpStatus, String code, String requestId, Object details, boolean retryable) {
        super(message, httpStatus, code, requestId, details, retryable);
    }
}
