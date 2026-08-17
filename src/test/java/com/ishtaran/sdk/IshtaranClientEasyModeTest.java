package com.ishtaran.sdk;

import com.ishtaran.sdk.error.TimeoutError;
import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa a lógica de COMPOSIÇÃO do Easy Mode de ponta a ponta (não só um resource isolado) — usa o
 * construtor package-private {@code IshtaranClient(HttpTransport)} para injetar
 * {@link FakeHttpTransport}, sem rede real.
 */
class IshtaranClientEasyModeTest {

    @Test
    void receivePayment_composesTransactionAndPaymentIntent_exposesRealCoreIds() {
        UUID transactionId = UUID.randomUUID();
        UUID paymentIntentId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID payerAccountId = UUID.randomUUID();
        UUID recipientAccountId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        var fake = new FakeHttpTransport()
                // 1. create transaction
                .enqueue(FakeHttpTransport.json(201, "{\"transactionId\":\"" + transactionId + "\"}"))
                // 2. create payment intent
                .enqueue(FakeHttpTransport.json(201, "{\"paymentIntentId\":\"" + paymentIntentId + "\"}"))
                // 3. getPayment -> get transaction
                .enqueue(FakeHttpTransport.json(200, transactionJson(transactionId, orgId, assetNetworkId, 0)))
                // 4. getPayment -> get payment intent
                .enqueue(FakeHttpTransport.json(200, paymentIntentJson(paymentIntentId, orgId, transactionId, assetNetworkId, 0, "TDeposit1real")));

        var client = new IshtaranClient(fake);

        var result = client.receivePayment(orgId, UUID.randomUUID(), payerAccountId, recipientAccountId,
                assetNetworkId, new BigDecimal("100"));

        assertEquals(transactionId, result.transactionId());
        assertEquals(paymentIntentId, result.paymentIntentId());
        assertEquals("TDeposit1real", result.depositAddress());
        assertEquals(4, fake.requestCount());
    }

    @Test
    void waitForPayment_pollsUntilPaid_returnsResult() {
        UUID transactionId = UUID.randomUUID();
        UUID paymentIntentId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        var fake = new FakeHttpTransport()
                // first poll: still Pending (status=0)
                .enqueue(FakeHttpTransport.json(200, transactionJson(transactionId, orgId, assetNetworkId, 0)))
                .enqueue(FakeHttpTransport.json(200, paymentIntentJson(paymentIntentId, orgId, transactionId, assetNetworkId, 0, "addr")))
                // second poll: now Paid (status=2)
                .enqueue(FakeHttpTransport.json(200, transactionJson(transactionId, orgId, assetNetworkId, 4)))
                .enqueue(FakeHttpTransport.json(200, paymentIntentJson(paymentIntentId, orgId, transactionId, assetNetworkId, 2, "addr")));

        var client = new IshtaranClient(fake);

        var result = client.waitForPayment(transactionId, paymentIntentId, Duration.ofSeconds(5), Duration.ofMillis(1));

        assertNotNull(result);
        // 2 polls x 2 requests/poll (getTransaction + getPaymentIntent) = 4: 1a Pending, 2a Paid.
        assertEquals(4, fake.requestCount());
    }

    @Test
    void waitForPayment_neverResolves_throwsTimeoutError_neverHangsForever() {
        UUID transactionId = UUID.randomUUID();
        UUID paymentIntentId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        var fake = new FakeHttpTransport().respondAlways(req -> req.path().contains("/transactions/")
                ? FakeHttpTransport.json(200, transactionJson(transactionId, orgId, assetNetworkId, 0))
                : FakeHttpTransport.json(200, paymentIntentJson(paymentIntentId, orgId, transactionId, assetNetworkId, 0, "addr")));

        var client = new IshtaranClient(fake);

        assertThrows(TimeoutError.class, () ->
                client.waitForPayment(transactionId, paymentIntentId, Duration.ofMillis(20), Duration.ofMillis(5)));
    }

    private static String transactionJson(UUID transactionId, UUID orgId, UUID assetNetworkId, int status) {
        return """
                {"transactionId":"%s","organizationId":"%s","applicationId":"%s","workflowVersionId":null,
                 "currentWorkflowStateId":null,"assetNetworkId":"%s","amount":100,"status":%d,
                 "payerAccountId":"%s","participants":[],"createdAt":"2026-08-17T12:00:00Z",
                 "settledAmount":0,"refundedAmount":0}
                """.formatted(transactionId, orgId, UUID.randomUUID(), assetNetworkId, status, UUID.randomUUID());
    }

    private static String paymentIntentJson(UUID paymentIntentId, UUID orgId, UUID transactionId, UUID assetNetworkId,
                                             int status, String depositAddress) {
        return """
                {"paymentIntentId":"%s","organizationId":"%s","transactionId":"%s","assetNetworkId":"%s",
                 "amount":100,"status":%d,"expiresAt":null,"depositAddress":"%s","deposits":[],
                 "createdAt":"2026-08-17T12:00:00Z"}
                """.formatted(paymentIntentId, orgId, transactionId, assetNetworkId, status, depositAddress);
    }
}
