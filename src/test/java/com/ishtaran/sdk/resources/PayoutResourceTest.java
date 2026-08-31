package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayoutResourceTest {

    @Test
    void getPayableSummary_readsAccruedReservedForPayoutPaid_neverAvailable() {
        UUID accountId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{\"accrued\":40,\"reservedForPayout\":0,\"paid\":60}"));
        var resource = new PayoutResource(fake);

        var summary = resource.getPayableSummary(accountId, assetNetworkId);

        assertEquals(new BigDecimal("40"), summary.accrued());
        assertEquals(new BigDecimal("0"), summary.reservedForPayout());
        assertEquals(new BigDecimal("60"), summary.paid());
        assertTrue(fake.received().get(0).path().equals("/v1/accounts/" + accountId + "/payable-summary?assetNetworkId=" + assetNetworkId));
    }

    @Test
    void createBatch_autoGeneratesAnIdempotencyKey_mapsTheCreatedId() {
        UUID payoutBatchId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, "{\"payoutBatchId\":\"" + payoutBatchId + "\"}"));
        var resource = new PayoutResource(fake);

        var result = resource.createBatch(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, null);

        assertEquals(payoutBatchId, result.payoutBatchId());
        assertTrue(fake.received().get(0).body().contains("idempotencyKey"));
    }

    @Test
    void createBatch_maps204NoContent_toANullPayoutBatchId_neverAnError() {
        var fake = new FakeHttpTransport().enqueue(new com.ishtaran.sdk.http.HttpResponse(204, java.util.Map.of(), ""));
        var resource = new PayoutResource(fake);

        var result = resource.createBatch(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(UUID.randomUUID(), UUID.randomUUID()), null);

        assertNull(result.payoutBatchId());
    }

    @Test
    void getBatch_mapsTheFullObligationTreeAndQuoteSnapshot() {
        UUID payoutBatchId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        String body = """
                {
                  "payoutBatchId": "%s", "organizationId": "%s", "environmentId": "%s", "assetNetworkId": "%s",
                  "trigger": 2, "status": 3,
                  "obligations": [{
                    "ownerId": "%s", "amount": 100,
                    "sourceObligations": [{"originReference": "settlement:s1", "amount": 100}],
                    "destinationAddress": "Txxx", "status": 1
                  }],
                  "networkExecutionQuoteSnapshot": {
                    "network": "TRON", "nativeExecutionCost": 6.3, "resourceAssetNetworkId": "%s", "quoteCurrency": "USDT",
                    "fx": 0.12, "totalCharged": 3.16456, "authorizedNativeCost": 6.3, "expiresAt": "2026-08-31T12:00:00Z"
                  },
                  "signingRequestId": "%s", "createdAt": "2026-08-31T11:00:00Z"
                }
                """.formatted(payoutBatchId, organizationId, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new PayoutResource(fake);

        var batch = resource.getBatch(organizationId, payoutBatchId);

        assertEquals("MANUAL", batch.trigger().name());
        assertEquals("COMPLETED", batch.status().name());
        assertEquals("CONFIRMED", batch.obligations().get(0).status().name());
        assertEquals(new BigDecimal("100"), batch.obligations().get(0).sourceObligations().get(0).amount());
        assertEquals(new BigDecimal("3.16456"), batch.networkExecutionQuoteSnapshot().totalCharged());
    }
}
