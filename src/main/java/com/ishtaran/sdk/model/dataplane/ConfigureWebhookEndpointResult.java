package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code secret} is shown only in this response — never recoverable afterward (same invariant as {@code GenerateApiKeyResult}). */
public record ConfigureWebhookEndpointResult(UUID webhookEndpointId, String secret) {
}
