package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 07 — Quote a withdrawal BEFORE committing the amount (pure read, never reserves balance).
 * Always exposes the real Network Fee, never hides it.
 */
public final class Example07WithdrawalQuote {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));
        UUID environmentId = UUID.fromString(System.getenv("ISHTARAN_ENVIRONMENT_ID"));
        UUID accountId = UUID.fromString(System.getenv("ISHTARAN_PAYER_ACCOUNT_ID"));
        UUID destinationId = UUID.fromString(System.getenv("ISHTARAN_WITHDRAWAL_DESTINATION_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var quote = client.withdrawals().quote(organizationId, environmentId, accountId, destinationId,
                assetNetworkId, new BigDecimal("50"));

        System.out.println("requestedAmount=" + quote.requestedAmount());
        // Under SelfCustody the beneficiary always receives the full requestedAmount --
        // estimatedNetworkFee is deprecated and always null. networkExecutionCost is the real
        // network cost (paid separately, per the registered NetworkCostPayerAccount, never
        // subtracted from what the beneficiary receives).
        System.out.println("estimatedRecipientAmount=" + quote.estimatedRecipientAmount());
        System.out.println("networkExecutionCost=" + quote.networkExecutionCost());
        System.out.println("expiresAt=" + quote.expiresAt());
    }
}
