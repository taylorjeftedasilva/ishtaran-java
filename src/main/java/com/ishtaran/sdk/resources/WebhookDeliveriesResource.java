package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.RedeliverWebhookResult;
import com.ishtaran.sdk.model.dataplane.WebhookDeliveryResponse;

import java.util.UUID;

/** Control Plane (gestão) — {@code WebhookDeliveries} (2 rotas reais, mesmo módulo {@code Notifications}). */
public final class WebhookDeliveriesResource extends ApiResourceSupport {

    public WebhookDeliveriesResource(HttpTransport transport) {
        super(transport);
    }

    public WebhookDeliveryResponse get(UUID webhookDeliveryId) {
        return execute(HttpRequest.get("/v1/webhook-deliveries/" + webhookDeliveryId), WebhookDeliveryResponse.class);
    }

    public RedeliverWebhookResult redeliver(UUID webhookDeliveryId) {
        return execute(HttpRequest.post("/v1/webhook-deliveries/" + webhookDeliveryId + "/redeliver", null, false),
                RedeliverWebhookResult.class);
    }
}
