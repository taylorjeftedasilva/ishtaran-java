package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

public record RotateApiKeyResult(UUID newApiKeyId, String plainTextKey) {
}
