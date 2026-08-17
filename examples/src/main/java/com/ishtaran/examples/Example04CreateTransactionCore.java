package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** 04 — Criar uma Transaction via Core API, com controle granular dos participantes. */
public final class Example04CreateTransactionCore {

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

        var participants = List.of(
                new ParticipantInput(payerAccountId, "payer", true, null),
                new ParticipantInput(recipientAccountId, "recipient", false, null));

        var created = client.transactions().create(organizationId, applicationId, null,
                assetNetworkId, new BigDecimal("250"), participants, null);

        System.out.println("transactionId=" + created.transactionId());

        var state = client.transactions().getState(created.transactionId());
        System.out.println("Status: " + state.status());
    }
}
