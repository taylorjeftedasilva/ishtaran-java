package com.ishtaran.sdk.error;

import com.ishtaran.sdk.http.HttpResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

/**
 * Translates a real error {@link HttpResponse} into the correct {@link IshtaranError} subtype — see
 * SDK_CAPABILITY_SPEC.md §6. 401/403 never have a body (§6.3); the other 4xx/5xx normally carry
 * {@link ProblemDetails}, but the mapper never throws if the body comes back empty/malformed — it falls
 * back to a generic {@link ApiError} instead of propagating a parsing error to the consumer.
 */
public final class ErrorMapper {

    private ErrorMapper() {
    }

    public static IshtaranError map(HttpResponse response) {
        int status = response.status();
        String requestId = firstNonNull(response.header("X-Request-Id"), response.header("X-Correlation-Id"));

        if (status == 401) {
            return new AuthenticationError("Authentication failure (401) — missing or invalid API Key or token.");
        }
        if (status == 403) {
            return new AuthorizationError("Not authorized (403) — valid credential, but no permission for this operation.");
        }

        ProblemDetails problem = tryParse(response.body());
        String code = problem != null ? problem.code() : null;
        String detail = problem != null && problem.detail() != null ? problem.detail() : "HTTP error " + status;

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
