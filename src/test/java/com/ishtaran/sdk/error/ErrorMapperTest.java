package com.ishtaran.sdk.error;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Ver SDK_CAPABILITY_SPEC.md §6 — cada status/code real mapeia para o subtipo certo. */
class ErrorMapperTest {

    @Test
    void status401_hasNoBody_mapsToAuthenticationError_withoutParsing() {
        var response = new HttpResponse(401, Map.of(), "");
        var error = ErrorMapper.map(response);

        assertInstanceOf(AuthenticationError.class, error);
        assertNull(error.code());
        assertNull(error.details());
    }

    @Test
    void status403_hasNoBody_mapsToAuthorizationError() {
        var response = new HttpResponse(403, Map.of(), null);
        var error = ErrorMapper.map(response);

        assertInstanceOf(AuthorizationError.class, error);
    }

    @Test
    void status400_validationError_singleJoinedString_neverStructuredArray() {
        String body = """
                {"status":400,"title":"Bad Request","detail":"Amount must be positive; AccountId is required","code":"VALIDATION_ERROR"}
                """;
        var error = ErrorMapper.map(FakeHttpTransport.json(400, body));

        assertInstanceOf(ValidationError.class, error);
        assertEquals("VALIDATION_ERROR", error.code());
        assertTrue(error.getMessage().contains("Amount must be positive; AccountId is required"));
    }

    @Test
    void status404_mapsToNotFoundError() {
        String body = """
                {"status":404,"title":"Not Found","detail":"Withdrawal not found","code":"NOT_FOUND"}
                """;
        var error = ErrorMapper.map(FakeHttpTransport.json(404, body));

        assertInstanceOf(NotFoundError.class, error);
    }

    @Test
    void status409_idempotencyConflict_mapsToIdempotencyConflictError_isAlsoConflictError() {
        String body = """
                {"status":409,"title":"Conflict","detail":"Idempotency key reused with a different payload","code":"IDEMPOTENCY_KEY_CONFLICT"}
                """;
        var error = ErrorMapper.map(FakeHttpTransport.json(409, body));

        assertInstanceOf(IdempotencyConflictError.class, error);
        assertInstanceOf(ConflictError.class, error);
    }

    @Test
    void status409_otherConflict_mapsToGenericConflictError_neverIdempotencySubtype() {
        String body = """
                {"status":409,"title":"Conflict","detail":"Something else conflicted","code":"SOME_OTHER_CONFLICT"}
                """;
        var error = ErrorMapper.map(FakeHttpTransport.json(409, body));

        assertInstanceOf(ConflictError.class, error);
        assertEquals(ConflictError.class, error.getClass());
    }

    @Test
    void status429_mapsToRateLimitError_withRetryAfterFromHeader() {
        String body = """
                {"type":"https://ishtaran.com/problems/rate-limited","title":"Too Many Requests","status":429,"code":"RATE_LIMITED"}
                """;
        var response = FakeHttpTransport.jsonWithHeaders(429, body, Map.of("Retry-After", List.of("7")));
        var error = ErrorMapper.map(response);

        assertInstanceOf(RateLimitError.class, error);
        assertEquals(7, ((RateLimitError) error).retryAfterSeconds());
        assertTrue(error.retryable());
    }

    @Test
    void status5xx_unrecognizedCode_fallsBackToApiError_neverLosesStatusOrDetail() {
        String body = """
                {"status":503,"title":"Service Unavailable","detail":"Downstream dependency down","code":"SOME_NEW_5XX_CODE"}
                """;
        var error = ErrorMapper.map(FakeHttpTransport.json(503, body));

        assertInstanceOf(ApiError.class, error);
        assertEquals(503, error.httpStatus());
        assertEquals("SOME_NEW_5XX_CODE", error.code());
        assertTrue(error.retryable());
    }

    @Test
    void malformedOrEmptyBody_neverThrowsParsingException_fallsBackToApiError() {
        var error = ErrorMapper.map(FakeHttpTransport.json(500, "not json at all {{{"));

        assertInstanceOf(ApiError.class, error);
        assertEquals(500, error.httpStatus());
    }
}
