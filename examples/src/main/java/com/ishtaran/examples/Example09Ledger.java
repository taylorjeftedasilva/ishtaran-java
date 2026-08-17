package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.util.UUID;

/** 09 — Consultar saldo e histórico de Ledger Entries (com paginação real via iterador lazy). */
public final class Example09Ledger {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID accountId = UUID.fromString(System.getenv("ISHTARAN_PAYER_ACCOUNT_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        var balance = client.getBalance(accountId, assetNetworkId);
        System.out.println("Available=" + balance.available() + " Pending=" + balance.pending()
                + " Reserved=" + balance.reserved());

        System.out.println("Últimas entradas do Ledger:");
        int count = 0;
        for (var entry : client.ledger().listAllEntries(accountId, assetNetworkId, null, null, null, 20)) {
            System.out.println("  " + entry.nature() + " " + entry.amount() + " (" + entry.originReference() + ")");
            if (++count >= 50) {
                break; // iterador é lazy -- nunca carrega tudo de uma vez, seguro interromper cedo
            }
        }
    }
}
