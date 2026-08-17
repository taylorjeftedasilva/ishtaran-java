package com.ishtaran.sdk.error;

import com.ishtaran.sdk.http.HttpResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

/**
 * Traduz uma {@link HttpResponse} de erro real para o subtipo {@link IshtaranError} correto — ver
 * SDK_CAPABILITY_SPEC.md §6. 401/403 nunca têm corpo (§6.3); os demais 4xx/5xx normalmente carregam
 * {@link ProblemDetails}, mas o mapper nunca lança se o corpo vier vazio/malformado — cai em
 * {@link ApiError} genérico em vez de propagar um erro de parsing para o consumidor.
 */
public final class ErrorMapper {

    private ErrorMapper() {
    }

    public static IshtaranError map(HttpResponse response) {
        int status = response.status();
        String requestId = firstNonNull(response.header("X-Request-Id"), response.header("X-Correlation-Id"));

        if (status == 401) {
            return new AuthenticationError("Falha de autenticação (401) — API Key ou token ausente/inválido.");
        }
        if (status == 403) {
            return new AuthorizationError("Não autorizado (403) — credencial válida, mas sem permissão para esta operação.");
        }

        ProblemDetails problem = tryParse(response.body());
        String code = problem != null ? problem.code() : null;
        String detail = problem != null && problem.detail() != null ? problem.detail() : "Erro HTTP " + status;

        if (status == 429) {
            Integer retryAfter = parseIntOrNull(response.header("Retry-After"));
            return new RateLimitError(detail, requestId, problem, retryAfter);
        }
        if (status == 400 && "VALIDATION_ERROR".equals(code)) {
            return new ValidationError(detail, requestId, problem);
        }
        if (status == 404) {
            return new NotFoundError(detail, requestId, problem);
        }
        if (status == 409 && "IDEMPOTENCY_KEY_CONFLICT".equals(code)) {
            return new IdempotencyConflictError(detail, requestId, problem);
        }
        if (status == 409) {
            return new ConflictError(detail, code, requestId, problem);
        }

        boolean retryable = status >= 500;
        return new ApiError(detail, status, code, requestId, problem, retryable);
    }

    private static ProblemDetails tryParse(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return JsonCodec.mapper().readValue(body, ProblemDetails.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer parseIntOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
