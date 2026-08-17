package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.util.UUID;

/** 02 — Criar uma Account e consultá-la de volta (Core API). */
public final class Example02CreateAccount {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));

        var created = client.accounts().create(organizationId, "customer-example-002");
        System.out.println("Account criada: " + created.accountId());

        var account = client.accounts().get(created.accountId());
        System.out.println("Status: " + account.status() + ", externalId=" + account.externalId());
    }
}
