package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/** {@code plainTextKey} is exposed ONLY ONCE, in this response — never recoverable afterward (same rule as the real backend). */
public record GenerateApiKeyResult(UUID apiKeyId, String plainTextKey) {
}
