package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

/**
 * 01 — Minimal quickstart: API key -> client -> first useful call. Deliberately short — it does
 * not require understanding the entire SDK architecture to take the first step (brief rule).
 */
public final class Example01Auth {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        System.out.println("Ishtaran client ready: " + client.accounts().getClass().getSimpleName() + " available.");
    }
}
