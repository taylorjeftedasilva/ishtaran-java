package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code secret} só é exibido nesta resposta — nunca recuperável depois (mesmo invariante de {@code GenerateApiKeyResult}). */
public record ConfigureWebhookEndpointResult(UUID webhookEndpointId, String secret) {
}
