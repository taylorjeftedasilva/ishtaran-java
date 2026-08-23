package com.ishtaran.sdk.error;

/** Connect or request/read timeout exceeded (see SDK_CAPABILITY_SPEC.md §7). Always retryable. */
public final class TimeoutError extends IshtaranError {
    public TimeoutError(String message, Throwable cause) {
        super(message, null, null, null, null, true);
        if (cause != null) {
            initCause(cause);
        }
    }
}
