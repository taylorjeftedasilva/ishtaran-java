package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code secret} is shown only in this response — never recoverable afterward. */
public record RotateWebhookEndpointSecretResult(UUID webhookEndpointId, String secret) {
}
