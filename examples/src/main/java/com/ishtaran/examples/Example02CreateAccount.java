package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.util.UUID;

/** 02 — Create an Account and query it back (Core API). */
public final class Example02CreateAccount {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));

        var created = client.accounts().create(organizationId, "customer-example-002");
        System.out.println("Account created: " + created.accountId());

        var account = client.accounts().get(created.accountId());
        System.out.println("Status: " + account.status() + ", accountHolderId=" + account.accountHolderId());

        // DEC-032 — Account no longer carries externalId/organizationId directly (global
        // identity, linked to N Organizations via Relationship). To see this Organization's
        // link to the Account (including externalId/authorized Applications), query the
        // Organization-scoped list:
        var relationships = client.accounts().list(organizationId);
        var own = relationships.stream().filter(r -> r.accountId().equals(created.accountId())).findFirst().orElseThrow();
        System.out.println("Relationship: " + own.relationshipId() + ", externalId=" + own.externalId() + ", status=" + own.relationshipStatus());
    }
}
