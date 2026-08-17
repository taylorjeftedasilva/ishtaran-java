package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApiKeyMetadataResponse(UUID apiKeyId, OffsetDateTime createdAt, OffsetDateTime lastUsedAt) {
}
