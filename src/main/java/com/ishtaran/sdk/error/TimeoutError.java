package com.ishtaran.sdk.error;

/** Connect ou request/read timeout excedido (ver SDK_CAPABILITY_SPEC.md §7). Sempre retryable. */
public final class TimeoutError extends IshtaranError {
    public TimeoutError(String message, Throwable cause) {
        super(message, null, null, null, null, true);
        if (cause != null) {
            initCause(cause);
        }
    }
}
