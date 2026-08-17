package com.ishtaran.sdk.error;

/**
 * 409, {@code code=IDEMPOTENCY_KEY_CONFLICT} — mesma chave reenviada com payload diferente do
 * original (ver SDK_CAPABILITY_SPEC.md §9). Subtipo de {@link ConflictError} para permitir
 * catch genérico OU específico.
 */
public final class IdempotencyConflictError extends ConflictError {
    public IdempotencyConflictError(String message, String requestId, Object details) {
        super(message, "IDEMPOTENCY_KEY_CONFLICT", requestId, details);
    }
}
