package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/**
 * Mirrors {@code CompositionRoot.EndpointMapping.SignUpResponse} exactly.
 *
 * <p>{@code apiKeyPlainText} is null when the signup is a replay of an already-used
 * {@code Idempotency-Key} — the API Key was already generated on the first call and is never
 * re-exposed.
 */
public record SignUpResponse(
        UUID organizationId,
        UUID memberId,
        TokenResult token,
        UUID applicationId,
        UUID environmentId,
        String apiKeyPlainText) {
}
