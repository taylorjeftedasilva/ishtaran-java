package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.error.NotFoundError;
import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.model.enums.WithdrawalStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Network-free integration (FakeHttpTransport) — covers real request/response serialization for the brief's most critical module. */
class WithdrawalsResourceTest {

    @Test
    void quote_neverWritesAnything_justReturnsEstimate_exposingNetworkFee() {
        UUID orgId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        String responseBody = """
                {
                  "accountId": "%s", "withdrawalDestinationId": "%s", "assetNetworkId": "%s",
                  "requestedAmount": 100, "estimatedNetworkFee": null, "estimatedRecipientAmount": 100,
                  "networkExecutionCost": 0.84364, "expiresAt": "2026-08-17T12:00:00Z"
                }
                """.formatted(accountId, destId, assetNetworkId);

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, responseBody));
        var resource = new WithdrawalsResource(fake);

        UUID environmentId = UUID.randomUUID();
        var quote = resource.quote(orgId, environmentId, accountId, destId, assetNetworkId, new BigDecimal("100"));

        assertEquals(null, quote.estimatedNetworkFee());
        assertEquals(new BigDecimal("100"), quote.estimatedRecipientAmount());
        assertEquals(new BigDecimal("0.84364"), quote.networkExecutionCost());
        assertEquals(1, fake.requestCount());
        assertEquals("POST", fake.received().get(0).method().name());
        assertTrue(fake.received().get(0).path().endsWith("/withdrawals/quote"));
        // Regression: GetWithdrawalQuoteQuery/RequestWithdrawalCommand require environmentId
        // server-side (RequestWithdrawalRequest.cs) -- omitting it defaults to Guid.Empty and
        // fails FluentValidation with a 400 VALIDATION_ERROR, confirmed live 2026-08-31.
        assertTrue(fake.received().get(0).body().contains(environmentId.toString()));
    }

    @Test
    void request_autoGeneratesIdempotencyKey_whenNotProvided() {
        UUID orgId = UUID.randomUUID();
        String responseBody = """
                {
                  "withdrawalId": "%s", "organizationId": "%s", "environmentId": "%s", "accountId": "%s",
                  "withdrawalDestinationId": "%s", "assetNetworkId": "%s",
                  "amount": 100, "estimatedNetworkFee": null, "estimatedRecipientAmount": 100,
                  "finalNetworkFee": null, "finalRecipientAmount": null,
                  "status": 0, "entryGroupId": null, "technicalReference": null,
                  "signingRequestId": "%s", "networkExecutionCost": 0.84364, "networkExecutionCostStatus": 0,
                  "createdAt": "2026-08-17T12:00:00Z"
                }
                """.formatted(UUID.randomUUID(), orgId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, responseBody));
        var resource = new WithdrawalsResource(fake);

        UUID environmentId = UUID.randomUUID();
        var result = resource.request(orgId, environmentId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("100"), null);

        assertEquals(WithdrawalStatus.REQUESTED, result.status());
        assertEquals(new BigDecimal("0.84364"), result.networkExecutionCost());
        HttpRequest sentRequest = fake.received().get(0);
        assertTrue(sentRequest.body().contains("idempotencyKey"));
        assertTrue(sentRequest.body().contains(environmentId.toString()));
    }

    @Test
    void get_notFound_mapsToNotFoundError() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(404,
                "{\"status\":404,\"title\":\"Not Found\",\"detail\":\"Withdrawal not found\",\"code\":\"NOT_FOUND\"}"));
        var resource = new WithdrawalsResource(fake);

        assertThrows(NotFoundError.class, () -> resource.get(UUID.randomUUID()));
    }
}
