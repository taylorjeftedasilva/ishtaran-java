package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code secret} só é exibido nesta resposta — nunca recuperável depois. */
public record RotateWebhookEndpointSecretResult(UUID webhookEndpointId, String secret) {
}
