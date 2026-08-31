package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

/** 08 — Execute a withdrawal via Easy Mode and wait (with timeout) for a terminal state. */
public final class Example08Withdrawal {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));
        UUID environmentId = UUID.fromString(System.getenv("ISHTARAN_ENVIRONMENT_ID"));
        UUID accountId = UUID.fromString(System.getenv("ISHTARAN_PAYER_ACCOUNT_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var withdrawal = client.withdraw(organizationId, environmentId, accountId, assetNetworkId,
                new BigDecimal("50"), "TDestinationAddressReal", null);

        System.out.println("withdrawalId=" + withdrawal.withdrawalId());
        // Under SelfCustody the beneficiary receives the full requested amount --
        // networkExecutionCost is the real network cost, charged separately to the registered
        // NetworkCostPayerAccount.
        System.out.println("You receive " + withdrawal.estimatedRecipientAmount()
                + " (network execution cost: " + withdrawal.networkExecutionCost() + ")");

        var finalState = client.withdrawals().waitFor(withdrawal.withdrawalId(), Duration.ofMinutes(15), Duration.ofSeconds(10));
        System.out.println("Final status: " + finalState.status());
    }
}
