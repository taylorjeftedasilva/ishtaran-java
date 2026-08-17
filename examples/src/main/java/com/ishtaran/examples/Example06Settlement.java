package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.util.UUID;

/** 06 — Liquidar uma Transaction financiada (Settlement) e consultar o resumo. */
public final class Example06Settlement {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID transactionId = UUID.fromString(System.getenv("ISHTARAN_TRANSACTION_ID"));

        var result = client.settlements().executeSettlement(transactionId, null);
        System.out.println("settlementId=" + result.settlementId());

        var settlement = client.settlements().get(result.settlementId());
        System.out.println("grossAmount=" + settlement.grossAmount()
                + ", platformFeeAmount=" + settlement.platformFeeAmount()
                + ", distributableAmount=" + settlement.distributableAmount());

        var summary = client.settlements().getSummary(transactionId);
        System.out.println("settledAmount=" + summary.settledAmount() + ", retainedAmount=" + summary.retainedAmount());
    }
}
