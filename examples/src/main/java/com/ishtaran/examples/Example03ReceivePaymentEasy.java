package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

/**
 * 03 — Receive a payment via Easy Mode: composes Transaction + Payment Intent and returns the
 * real depositAddress, with the real Core IDs (transactionId/paymentIntentId) for debugging.
 */
public final class Example03ReceivePaymentEasy {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));
        UUID applicationId = UUID.fromString(System.getenv("ISHTARAN_APPLICATION_ID"));
        UUID payerAccountId = UUID.fromString(System.getenv("ISHTARAN_PAYER_ACCOUNT_ID"));
        UUID recipientAccountId = UUID.fromString(System.getenv("ISHTARAN_RECIPIENT_ACCOUNT_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var payment = client.receivePayment(organizationId, applicationId, payerAccountId,
                recipientAccountId, assetNetworkId, new BigDecimal("100"));

        System.out.println("transactionId=" + payment.transactionId());
        System.out.println("paymentIntentId=" + payment.paymentIntentId());
        System.out.println("depositAddress=" + payment.depositAddress());

        var finished = client.waitForPayment(payment.transactionId(), payment.paymentIntentId(),
                Duration.ofMinutes(10), Duration.ofSeconds(5));
        System.out.println("Final status: " + finished.paymentIntentStatus());
    }
}
