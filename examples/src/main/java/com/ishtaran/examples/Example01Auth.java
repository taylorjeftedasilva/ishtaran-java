package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

/**
 * 01 — Quickstart mínimo: API key -> client -> primeira chamada útil. Deliberadamente curto — não
 * exige entender toda a arquitetura do SDK para dar o primeiro passo (regra do brief).
 */
public final class Example01Auth {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        System.out.println("Client Ishtaran pronto: " + client.accounts().getClass().getSimpleName() + " disponível.");
    }
}
