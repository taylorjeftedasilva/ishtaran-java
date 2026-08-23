package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code Redeliver} returns {@code { webhookDeliveryId } } (real anonymous object, see NotificationsEndpoints.cs). */
public record RedeliverWebhookResult(UUID webhookDeliveryId) {
}
