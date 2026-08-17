package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.WebhookEndpointStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WebhookEndpointResponse(UUID webhookEndpointId, UUID organizationId, String url,
                                       WebhookEndpointStatus status, OffsetDateTime createdAt) {
}
