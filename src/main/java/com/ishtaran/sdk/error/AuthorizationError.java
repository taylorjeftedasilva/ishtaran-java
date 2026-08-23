package com.ishtaran.sdk.error;

/** 403 — no JSON body (see SDK_CAPABILITY_SPEC.md §6.3). {@code code}/{@code details} are always null. */
public final class AuthorizationError extends IshtaranError {
    public AuthorizationError(String message) {
        super(message, 403, null, null, null, false);
    }
}
