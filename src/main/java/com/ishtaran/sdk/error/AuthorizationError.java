package com.ishtaran.sdk.error;

/** 403 — sem corpo JSON (ver SDK_CAPABILITY_SPEC.md §6.3). {@code code}/{@code details} são sempre nulos. */
public final class AuthorizationError extends IshtaranError {
    public AuthorizationError(String message) {
        super(message, 403, null, null, null, false);
    }
}
