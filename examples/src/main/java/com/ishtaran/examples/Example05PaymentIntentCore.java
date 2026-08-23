package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 05 — Create a Payment Intent via Core API. The real depositAddress only appears in the
 * dedicated GET (never in the creation POST response — real API behavior, not an SDK limitation).
 */
public final class Example05PaymentIntentCore {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));
        UUID transactionId = UUID.fromString(System.getenv("ISHTARAN_TRANSACTION_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var created = client.deposits().createPaymentIntent(
                organizationId, transactionId, assetNetworkId, new BigDecimal("250"), null, null);

        var full = client.deposits().getPaymentIntent(created.paymentIntentId());
        System.out.println("depositAddress=" + full.depositAddress());
        System.out.println("status=" + full.status());
    }
}
