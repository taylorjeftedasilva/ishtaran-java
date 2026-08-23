package com.ishtaran.sdk.error;

/**
 * SDK Self-Custody Capability (SPEC-018/019 SDK Impact Matrix §2) — thrown by client-side
 * validation of a {@code SigningRequest}/wallet operation before any signature is produced. Never
 * an HTTP response error (no {@code httpStatus}/{@code requestId}) — this is a pre-flight, local
 * rejection: the SDK never signs when in doubt (fail-closed).
 */
public class InvalidSigningRequestError extends IshtaranError {

    public InvalidSigningRequestError(String message) {
        super(message, null, "INVALID_SIGNING_REQUEST", null, null, false);
    }

    public InvalidSigningRequestError(String message, Throwable cause) {
        this(message);
        initCause(cause);
    }
}
