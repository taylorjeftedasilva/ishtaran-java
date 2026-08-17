package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

/** 08 — Executar um saque via Easy Mode e esperar (com timeout) até um estado terminal. */
public final class Example08Withdrawal {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));
        UUID accountId = UUID.fromString(System.getenv("ISHTARAN_PAYER_ACCOUNT_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var withdrawal = client.withdraw(organizationId, accountId, assetNetworkId,
                new BigDecimal("50"), "TDestinationAddressReal", null);

        System.out.println("withdrawalId=" + withdrawal.withdrawalId());
        System.out.println("Você recebe " + withdrawal.estimatedRecipientAmount()
                + " (taxa de rede: " + withdrawal.estimatedNetworkFee() + ")");

        var finalState = client.withdrawals().waitFor(withdrawal.withdrawalId(), Duration.ofMinutes(15), Duration.ofSeconds(10));
        System.out.println("Status final: " + finalState.status());
    }
}
