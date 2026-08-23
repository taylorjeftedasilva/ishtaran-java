package com.ishtaran.sdk.error;

/** Transport failure — no HTTP response at all (connection refused/reset, DNS, etc.). Always retryable. */
public final class NetworkError extends IshtaranError {
    public NetworkError(String message, Throwable cause) {
        super(message, null, null, null, null, true);
        if (cause != null) {
            initCause(cause);
        }
    }
}
