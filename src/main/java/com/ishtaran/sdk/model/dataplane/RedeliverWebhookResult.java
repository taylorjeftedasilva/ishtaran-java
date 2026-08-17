package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code Redeliver} devolve {@code { webhookDeliveryId } } (objeto anônimo real, ver NotificationsEndpoints.cs). */
public record RedeliverWebhookResult(UUID webhookDeliveryId) {
}
