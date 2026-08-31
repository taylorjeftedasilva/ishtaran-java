package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.dataplane.NetworkExecutionOperationInput;
import com.ishtaran.sdk.model.enums.NetworkCostPayer;
import com.ishtaran.sdk.model.enums.NetworkOperationKind;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkExecutionResourceTest {

    @Test
    void quote_postsOperationsAndNetworkCostPayer_mapsTheFullStructuredPlan_inc18ScalingProof() {
        UUID environmentId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        String responseBody = """
                {
                  "network": "TRON",
                  "plan": {
                    "assetNetworkId": "%s",
                    "transactions": [
                      {"transfers": [{"destinationAddress": "Txxx1", "amount": 40, "sourceOperationReference": "op-1"}]},
                      {"transfers": [{"destinationAddress": "Txxx2", "amount": 60, "sourceOperationReference": "op-2"}]}
                    ]
                  },
                  "estimatedResources": {"lines": [{"resourceCode": "ENERGY", "quantity": 15000, "unit": null}, {"resourceCode": "BANDWIDTH", "quantity": 350, "unit": null}]},
                  "nativeExecutionCost": 6.3,
                  "resourceAssetNetworkId": "%s",
                  "quoteCurrency": "USDT",
                  "fx": 0.12,
                  "safetyBuffer": 0.05,
                  "resourceSource": 1,
                  "replenishmentRequirement": null,
                  "conversionOverhead": 0.02,
                  "expiresAt": "2026-08-31T12:00:00Z",
                  "totalCharged": 3.16456,
                  "networkCostPayer": 0,
                  "authorizedNativeCost": 6.3
                }
                """.formatted(assetNetworkId, UUID.randomUUID());

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, responseBody));
        var resource = new NetworkExecutionResource(fake);

        var operations = List.of(
                new NetworkExecutionOperationInput("Txxx1", new BigDecimal("40"), NetworkOperationKind.TRANSFER, "op-1"),
                new NetworkExecutionOperationInput("Txxx2", new BigDecimal("60"), NetworkOperationKind.TRANSFER, "op-2"));

        var quote = resource.quote(environmentId, assetNetworkId, operations, NetworkCostPayer.INTEGRATOR);

        assertEquals("POST", fake.received().get(0).method().name());
        assertTrue(fake.received().get(0).path().endsWith("/network-execution-quote"));
        assertTrue(fake.received().get(0).body().contains("\"networkCostPayer\":0"));

        assertEquals(2, quote.plan().transactions().size());
        assertEquals(new BigDecimal("3.16456"), quote.totalCharged());
        assertEquals(new BigDecimal("6.3"), quote.authorizedNativeCost());
        assertEquals("SELF", quote.resourceSource().name());
        assertEquals("INTEGRATOR", quote.networkCostPayer().name());
    }

    @Test
    void quote_acceptsNullOperations_aSizeOnlyEstimate() {
        String responseBody = """
                {
                  "network": "TRON", "plan": {"assetNetworkId": "%s", "transactions": []},
                  "estimatedResources": {"lines": []}, "nativeExecutionCost": 0, "resourceAssetNetworkId": null,
                  "quoteCurrency": null, "fx": 1, "safetyBuffer": 0, "resourceSource": 0, "replenishmentRequirement": null,
                  "conversionOverhead": 0, "expiresAt": "2026-08-31T12:00:00Z", "totalCharged": 0, "networkCostPayer": 1,
                  "authorizedNativeCost": 0
                }
                """.formatted(UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, responseBody));
        var resource = new NetworkExecutionResource(fake);

        var quote = resource.quote(UUID.randomUUID(), UUID.randomUUID(), null, NetworkCostPayer.REQUESTER);

        assertTrue(fake.received().get(0).body().contains("\"operations\":null"));
        assertNull(quote.replenishmentRequirement());
    }
}
