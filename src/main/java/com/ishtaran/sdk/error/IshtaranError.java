package com.ishtaran.sdk.error;

/**
 * Base de toda exceção lançada pelo SDK — ver SDK_CAPABILITY_SPEC.md §6.4. {@code httpStatus}/
 * {@code code} são nulos para {@link NetworkError}/{@link TimeoutError} (nenhuma resposta HTTP
 * existiu); {@code code}/{@code details} são sempre nulos para {@link AuthenticationError}/
 * {@link AuthorizationError} (401/403 nunca têm corpo — ver §6.3, Known Gap §12.1).
 */
public class IshtaranError extends RuntimeException {

    private final Integer httpStatus;
    private final String code;
    private final String requestId;
    private final Object details;
    private final boolean retryable;

    public IshtaranError(String message, Integer httpStatus, String code, String requestId,
                          Object details, boolean retryable) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.requestId = requestId;
        this.details = details;
        this.retryable = retryable;
    }

    public Integer httpStatus() {
        return httpStatus;
    }

    /** Chave estável de erro de domínio (ex.: {@code VALIDATION_ERROR}) — nulo quando não aplicável. */
    public String code() {
        return code;
    }

    /**
     * Sempre nulo hoje — a API real não implementa nenhum mecanismo de request/correlation ID
     * (busca exaustiva em src/CompositionRoot/, zero ocorrência — ver SDK_CAPABILITY_SPEC.md
     * §12.1). Campo mantido para quando esse mecanismo existir no backend, sem breaking change.
     */
    public String requestId() {
        return requestId;
    }

    public Object details() {
        return details;
    }

    public boolean retryable() {
        return retryable;
    }
}
