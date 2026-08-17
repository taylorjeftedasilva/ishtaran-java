package com.ishtaran.sdk.error;

/** 404, {@code code=NOT_FOUND}. */
public final class NotFoundError extends IshtaranError {
    public NotFoundError(String message, String requestId, Object details) {
        super(message, 404, "NOT_FOUND", requestId, details, false);
    }
}
