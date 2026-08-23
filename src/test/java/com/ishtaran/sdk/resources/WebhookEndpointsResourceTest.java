package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.enums.WebhookDeliveryStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Confirms that the deliveries {@code status} filter goes as a NAME (string, case-insensitive) —
 * unlike the {@code AssetNetworkCatalog} filter (integer). Confirmed in the real source code:
 * {@code Enum.Parse<WebhookDeliveryStatus>(status, ignoreCase: true)} in NotificationsEndpoints.cs.
 */
class WebhookEndpointsResourceTest {

    @Test
    void listDeliveries_statusFilter_sentAsStringName_notInteger() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new WebhookEndpointsResource(fake);

        resource.listDeliveries(UUID.randomUUID(), null, WebhookDeliveryStatus.DELIVERED);

        var path = fake.received().get(0).path();
        assertTrue(path.contains("status=DELIVERED"), "expected the string name, not '2': " + path);
    }

    @Test
    void listDeliveries_eventTypeFilter_isUrlEncoded_neverInjectsExtraQueryParams() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new WebhookEndpointsResource(fake);

        resource.listDeliveries(UUID.randomUUID(), "payment.received&status=DELIVERED", null);

        var path = fake.received().get(0).path();
        assertTrue(path.contains("eventType=payment.received%26status%3DDELIVERED"),
                "'&'/'=' in the value must be URL-encoded, never injecting a second parameter: " + path);
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
