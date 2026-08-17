package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkflowResponse(UUID workflowId, UUID organizationId, String name, String status,
                                OffsetDateTime createdAt, List<UUID> versionIds) {
}
