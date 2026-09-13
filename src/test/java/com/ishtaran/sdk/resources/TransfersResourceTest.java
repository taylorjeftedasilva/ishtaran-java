package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransfersResourceTest {

    @Test
    void request_toInternalAccount_mapsAwaitingSignatureAndSigningRequestId() {
        // PROMPT 7.1/BR-TRF-008 (found live 2026-09-12) -- the creation endpoint itself only ever
        // acknowledges {"transferId": ...} (same convention as every other POST .../transfers-shaped
        // route in this platform) -- request() must follow up with a real GET to return a populated result.
        UUID orgId = UUID.randomUUID();
        UUID appId = UUID.randomUUID();
        UUID envId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        UUID signingRequestId = UUID.randomUUID();
        String createAck = "{\"transferId\":\"" + transferId + "\"}";
        String fullBody = """
                {"transferId":"%s","organizationId":"%s","applicationId":"%s","environmentId":"%s",
                 "sourceAccountId":"%s","assetNetworkId":"%s","amount":100,"destinationAddress":"TRecipient...",
                 "destinationAccountId":"%s","platformFeeAmount":0.2,"platformFeePercentage":0.2,
                 "status":1,"signingRequestId":"%s","createdAt":"2026-09-12T12:00:00Z","confirmedAt":null,"failureReason":null}
                """.formatted(transferId, orgId, appId, envId, sourceAccountId, assetNetworkId, destinationAccountId, signingRequestId);
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, createAck)).enqueue(FakeHttpTransport.json(200, fullBody));
        var resource = new TransfersResource(fake);

        var result = resource.request(orgId, appId, envId, sourceAccountId, assetNetworkId, new BigDecimal("100"),
                destinationAccountId, null, null);

        assertEquals("AWAITING_SIGNATURE", result.status().name());
        assertEquals(signingRequestId, result.signingRequestId());
        // BR-TRF-004 -- amount is exactly what the recipient receives; the fee is separate.
        assertEquals(new BigDecimal("100"), result.amount());
        assertEquals(new BigDecimal("0.2"), result.platformFeeAmount());
        assertEquals(2, fake.received().size());
        assertTrue(fake.received().get(0).path().equals("/v1/organizations/" + orgId + "/transfers"));
        assertTrue(fake.received().get(1).path().equals("/v1/transfers/" + transferId));
    }

    @Test
    void request_toExternalAddress_neverRequiresPreRegistration() {
        UUID orgId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        String createAck = "{\"transferId\":\"" + transferId + "\"}";
        String fullBody = """
                {"transferId":"%s","organizationId":"%s","applicationId":"%s","environmentId":"%s",
                 "sourceAccountId":"%s","assetNetworkId":"%s","amount":50,"destinationAddress":"TExternalAddress...",
                 "destinationAccountId":null,"platformFeeAmount":0.1,"platformFeePercentage":0.2,
                 "status":0,"signingRequestId":null,"createdAt":"2026-09-12T12:00:00Z","confirmedAt":null,"failureReason":null}
                """.formatted(transferId, orgId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, createAck)).enqueue(FakeHttpTransport.json(200, fullBody));
        var resource = new TransfersResource(fake);

        var result = resource.request(orgId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("50"), null, "TExternalAddress...", "my-key");

        assertNull(result.destinationAccountId());
        assertEquals("TExternalAddress...", result.destinationAddress());
        assertNull(result.signingRequestId());
    }

    @Test
    void get_mapsAFailedTransfer_exposingFailureReason() {
        UUID transferId = UUID.randomUUID();
        String body = """
                {"transferId":"%s","organizationId":"%s","applicationId":"%s","environmentId":"%s",
                 "sourceAccountId":"%s","assetNetworkId":"%s","amount":100,"destinationAddress":"TX...",
                 "destinationAccountId":null,"platformFeeAmount":0.2,"platformFeePercentage":0.2,
                 "status":4,"signingRequestId":"%s","createdAt":"2026-09-12T12:00:00Z","confirmedAt":null,"failureReason":"Insufficient balance"}
                """.formatted(transferId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new TransfersResource(fake);

        var result = resource.get(transferId);

        assertEquals("FAILED", result.status().name());
        assertEquals("Insufficient balance", result.failureReason());
    }
}
