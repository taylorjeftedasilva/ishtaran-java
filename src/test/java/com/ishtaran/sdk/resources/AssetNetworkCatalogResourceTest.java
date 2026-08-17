package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.enums.AssetNetworkStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Confirma que o filtro de {@code status} vai como INTEIRO na query string, per o contrato real do
 * OpenAPI (diferente do filtro de status de Webhook Deliveries, que é string — ver
 * SDK_CAPABILITY_SPEC.md §11.3/{@link WebhookEndpointsResourceTest}).
 */
class AssetNetworkCatalogResourceTest {

    @Test
    void listAssetNetworks_statusFilter_sentAsRawInteger() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new AssetNetworkCatalogResource(fake);

        resource.listAssetNetworks(AssetNetworkStatus.PAUSED);

        assertTrue(fake.received().get(0).path().contains("status=2"),
                "PAUSED deve virar '2' na query string (contrato real: enum: [1,2,3], type: integer)");
    }

    @Test
    void listAssetNetworks_noFilter_omitsStatusParam() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new AssetNetworkCatalogResource(fake);

        resource.listAssetNetworks(null);

        assertTrue(!fake.received().get(0).path().contains("status="));
    }
}
