package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

import java.util.UUID;

/**
 * 12 — Invite an AccountHolder to relate to an Organization, and claim the invitation from the
 * holder's side (DEC-032). Two "personas" in the same process purely for teaching purposes — in
 * real life, the {@code plainTextToken} goes out through a separate channel (email/link) and it
 * is the holder themself who calls {@code signUpAndClaimInvitation}, never the Organization on
 * their behalf.
 */
public final class Example12AccountHolderInvitation {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .build();

        UUID organizationId = UUID.fromString(System.getenv("ISHTARAN_ORGANIZATION_ID"));

        // Organization side: issues the invitation. plainTextToken only exists in this response —
        // treat it as a secret, deliver it to the holder outside the API (never log/persist it in
        // plain text).
        var invitation = client.accounts().createAccountHolderInvitation(organizationId, "customer-example-012");
        System.out.println("Invitation issued: " + invitation.invitationId() + ", expires at " + invitation.expiresAt());

        // Holder (AccountHolder) side: never seen before, creates the identity and claims the
        // invitation atomically. No prior authentication — the invitation token itself is proof
        // of possession.
        var claim = client.accountHolders().signUpAndClaimInvitation(
                invitation.plainTextToken(), "holder-example-012@example.com", "Str0ngP@ssw0rd!");

        if (!claim.success()) {
            throw new IllegalStateException("Failed to claim invitation: " + claim.errorCode());
        }
        System.out.println("Relationship created: " + claim.relationshipId());

        // The AccessToken returned (via signUpAndClaimInvitation) already populated the
        // AccountHolder session on this client — me() works immediately, no need to call login() again.
        var me = client.accountHolders().me();
        System.out.println("AccountHolder: " + me.accountHolderId() + ", account=" + me.accountId() + ", email=" + me.email());
    }
}
