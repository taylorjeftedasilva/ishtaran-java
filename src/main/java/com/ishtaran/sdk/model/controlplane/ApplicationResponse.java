package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

public record ApplicationResponse(UUID applicationId, UUID organizationId, String name, String status) {
}
