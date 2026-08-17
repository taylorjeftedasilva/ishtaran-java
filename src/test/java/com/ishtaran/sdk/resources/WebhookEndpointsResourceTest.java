package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.enums.WebhookDeliveryStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Confirma que o filtro de {@code status} de deliveries vai como NOME (string, case-insensitive) —
 * diferente do filtro de {@code AssetNetworkCatalog} (inteiro). Confirmado em código-fonte real:
 * {@code Enum.Parse<WebhookDeliveryStatus>(status, ignoreCase: true)} em NotificationsEndpoints.cs.
 */
class WebhookEndpointsResourceTest {

    @Test
    void listDeliveries_statusFilter_sentAsStringName_notInteger() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new WebhookEndpointsResource(fake);

        resource.listDeliveries(UUID.randomUUID(), null, WebhookDeliveryStatus.DELIVERED);

        var path = fake.received().get(0).path();
        assertTrue(path.contains("status=DELIVERED"), "esperado nome da string, não '2': " + path);
    }

    @Test
    void listDeliveries_eventTypeFilter_isUrlEncoded_neverInjectsExtraQueryParams() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new WebhookEndpointsResource(fake);

        resource.listDeliveries(UUID.randomUUID(), "payment.received&status=DELIVERED", null);

        var path = fake.received().get(0).path();
        assertTrue(path.contains("eventType=payment.received%26status%3DDELIVERED"),
                "'&'/'=' no valor devem ser URL-encoded, nunca injetar um segundo parâmetro: " + path);
    }

    @Test
    void create_exposesSecretOnlyOnce_realResponseShape() {
        UUID endpointId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201,
                "{\"webhookEndpointId\":\"" + endpointId + "\",\"secret\":\"whsec_abc123\"}"));
        var resource = new WebhookEndpointsResource(fake);

        var result = resource.create(UUID.randomUUID(), "https://example.com/webhook");

        assertEquals(endpointId, result.webhookEndpointId());
        assertEquals("whsec_abc123", result.secret());
    }
}
