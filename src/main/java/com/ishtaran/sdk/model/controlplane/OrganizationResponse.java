package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrganizationResponse(UUID organizationId, String name, String status, OffsetDateTime createdAt) {
}
