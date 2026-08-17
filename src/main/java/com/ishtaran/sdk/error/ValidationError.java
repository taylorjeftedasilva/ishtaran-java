package com.ishtaran.sdk.error;

/**
 * 400, {@code code=VALIDATION_ERROR}. {@code message()}/{@code details()} carregam UMA string com
 * todos os erros unidos por "; " — a API real (FluentValidation) não expõe array por campo (ver
 * SDK_CAPABILITY_SPEC.md §6.1/§12.2). Este SDK nunca finge uma estrutura por campo que não existe.
 */
public final class ValidationError extends IshtaranError {
    public ValidationError(String message, String requestId, Object details) {
        super(message, 400, "VALIDATION_ERROR", requestId, details, false);
    }
}
