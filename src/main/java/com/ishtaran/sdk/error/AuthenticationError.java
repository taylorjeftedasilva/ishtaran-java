package com.ishtaran.sdk.error;

/** 401 — sem corpo JSON (ver SDK_CAPABILITY_SPEC.md §6.3). {@code code}/{@code details} são sempre nulos. */
public final class AuthenticationError extends IshtaranError {
    public AuthenticationError(String message) {
        super(message, 401, null, null, null, false);
    }
}
