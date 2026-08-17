package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 11 — Fluxo completo de Sandbox: credita saldo de teste via Faucet e confirma. Nunca funciona
 * contra Production real (o backend rejeita simulações fora de um Environment do tipo Sandbox).
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
        System.out.println("Confirmação simulada — o Deposit real será processado via Outbox (assíncrono).");

        var treasuryBalance = client.sandbox().getTreasuryBalance(environmentId, assetNetworkId);
        System.out.println("Treasury observada: " + treasuryBalance.balance());
    }
}
