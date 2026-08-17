package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventTypeResponse(UUID eventTypeId, UUID organizationId, String name, OffsetDateTime createdAt) {
}
