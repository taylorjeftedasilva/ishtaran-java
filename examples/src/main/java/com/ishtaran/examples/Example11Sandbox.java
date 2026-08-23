package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 11 — Full Sandbox flow: credits a test balance via Faucet and confirms it. Never works
 * against real Production (the backend rejects simulations outside a Sandbox-type Environment).
 */
public final class Example11Sandbox {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID environmentId = UUID.fromString(System.getenv("ISHTARAN_SANDBOX_ENVIRONMENT_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var observedAddress = client.sandbox().faucet(environmentId, "TDepositAddressReal",
                assetNetworkId, new BigDecimal("100"));
        System.out.println("sandboxObservedAddressId=" + observedAddress.sandboxObservedAddressId());

        client.sandbox().simulateConfirmation(environmentId, observedAddress.sandboxObservedAddressId(), 3, true);
        System.out.println("Confirmation simulated — the real Deposit will be processed via Outbox (asynchronously).");

        var treasuryBalance = client.sandbox().getTreasuryBalance(environmentId, assetNetworkId);
        System.out.println("Observed Treasury: " + treasuryBalance.balance());
    }
}
