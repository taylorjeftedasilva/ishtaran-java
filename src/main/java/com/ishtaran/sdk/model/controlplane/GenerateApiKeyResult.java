package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/** {@code plainTextKey} só é exposto UMA vez, nesta resposta — nunca recuperável depois (mesma regra do backend real). */
public record GenerateApiKeyResult(UUID apiKeyId, String plainTextKey) {
}
