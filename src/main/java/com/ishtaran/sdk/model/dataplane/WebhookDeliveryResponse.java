package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.WebhookDeliveryStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WebhookDeliveryResponse(
        UUID webhookDeliveryId,
        UUID webhookEndpointId,
        String eventType,
        long sequenceNumber,
        WebhookDeliveryStatus status,
        int attemptCount,
        int maxAttempts,
        OffsetDateTime nextAttemptAt,
        OffsetDateTime lastAttemptAt,
        String lastError,
        UUID redeliveredFromId,
        OffsetDateTime createdAt) {
}
