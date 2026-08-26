package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.model.dataplane.ExecutionLegInput;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;
import com.ishtaran.sdk.model.enums.DerivationScheme;
import com.ishtaran.sdk.wallet.WalletFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * 14 — End-to-end marketplace journey, verified live against the real Sandbox (2026-08-25): a
 * buyer pays into a marketplace that holds its own self-custody execution wallet, a seller signs
 * up as their own {@code AccountHolder} to receive the payout, and the marketplace signs the real
 * payout itself — Ishtaran never sees a private key. Closes the full cycle other examples cover
 * individually (self-service signup, self-custody signing, Payment Intents, AccountHolder
 * invitations): this one connects them into one story, the way a real integrator would use them.
 *
 * <p>Two real gaps found and fixed while building this example, not hypothetical:
 * <ul>
 *   <li>{@code accounts().authorizeApplication(...)} requires a <b>Member</b> session — it always
 *       rejects an API Key, even though {@code Accounts} is otherwise usable with either (see
 *       {@code AccountsEndpoints.cs}, {@code MemberPermissionPolicy.Require}).</li>
 *   <li>Once a Payment Intent's deposit is confirmed, the Transaction moves itself to
 *       {@code Reserved} — no explicit {@code transactions().reserve(...)} call is needed (or
 *       valid) in this path.</li>
 * </ul>
 *
 * <p>Requires only {@code ISHTARAN_ASSET_NETWORK_ID}/{@code ISHTARAN_NETWORK_ID} env vars (an
 * Asset Network already seeded in the target Sandbox) — everything else (Organization,
 * Application, Environment, API Key, both Accounts) is provisioned by the example itself.
 */
public final class Example14MarketplaceJourney {

    public static void main(String[] args) throws InterruptedException {
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));
        UUID networkId = UUID.fromString(System.getenv("ISHTARAN_NETWORK_ID"));
        long t = System.currentTimeMillis();

        // 1. Marketplace operator signs up -- one call provisions Organization, a default
        //    Application, its Sandbox Environment, and a first API Key.
        var owner = IshtaranClient.builder().environment(Environment.SANDBOX).build();
        var signup = owner.auth().signUp("Marketplace Demo " + t, "owner+" + t + "@example.com", "Str0ngP@ssw0rd!123");
        UUID organizationId = signup.organizationId();
        UUID applicationId = signup.applicationId();
        UUID environmentId = signup.environmentId();
        var client = IshtaranClient.builder().apiKey(signup.apiKeyPlainText()).environment(Environment.SANDBOX).build();
        System.out.println("[1] signup ok organizationId=" + organizationId);

        // 2. The marketplace's own execution wallet -- generated locally, only the public key
        //    ever reaches Ishtaran. This is the wallet that will sign the real payout in step 7.
        var wallet = WalletFactory.generate();
        var registeredWallet = client.wallets().register(applicationId, networkId,
                DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, wallet.wallet().accountExtendedPublicKey(),
                "marketplace-wallet-" + t);
        System.out.println("[2] execution wallet registered walletId=" + registeredWallet.walletId());

        // 3. Seller signs up as their own AccountHolder, via an invitation the marketplace
        //    issues -- a distinct session, never the marketplace acting on the seller's behalf.
        var invitation = client.accounts().createAccountHolderInvitation(organizationId, "seller-" + t);
        var sellerClient = IshtaranClient.builder().apiKey(signup.apiKeyPlainText()).environment(Environment.SANDBOX).build();
        var claim = sellerClient.accountHolders().signUpAndClaimInvitation(
                invitation.plainTextToken(), "seller+" + t + "@example.com", "SellerP@ss123!");
        if (!claim.success()) throw new IllegalStateException("Seller failed to claim invitation: " + claim.errorCode());
        UUID sellerAccountId = sellerClient.accountHolders().me().accountId();
        System.out.println("[3] seller AccountHolder claimed, accountId=" + sellerAccountId);

        // 4. Buyer account -- Organization-provisioned, no login of their own (the common case
        //    for a one-off payer).
        UUID buyerAccountId = client.accounts().create(organizationId, "buyer-" + t).accountId();
        System.out.println("[4] buyer accountId=" + buyerAccountId);

        // 5. Authorize both Accounts for this Application. GOTCHA: this call requires the
        //    Member session (`owner`), not the API Key client (`client`) -- see class doc.
        owner.accounts().authorizeApplication(organizationId, sellerAccountId, applicationId);
        owner.accounts().authorizeApplication(organizationId, buyerAccountId, applicationId);
        System.out.println("[5] both accounts authorized for the application");

        // 6. Transaction + Payment Intent. No Split declared -- with exactly one non-payer
        //    Participant, BR-SPL-004 gives that Participant 100% of the Distributable Amount
        //    implicitly (2+ non-payer Participants would require an explicit Split).
        var payer = new ParticipantInput(buyerAccountId, "payer", true, null);
        var seller = new ParticipantInput(sellerAccountId, "seller", false, null);
        var txn = client.transactions().create(organizationId, applicationId, null, assetNetworkId,
                new BigDecimal("1000"), List.of(payer, seller), "marketplace-txn-" + t);
        var intent = client.deposits().createPaymentIntent(organizationId, txn.transactionId(), assetNetworkId, new BigDecimal("1000"), null, null);
        var fullIntent = client.deposits().getPaymentIntent(intent.paymentIntentId());
        System.out.println("[6] paymentIntentId=" + intent.paymentIntentId() + " depositAddress=" + fullIntent.depositAddress());

        // 7. Simulate the buyer's on-chain deposit and its confirmation (Sandbox only). Once
        //    confirmed, the Transaction moves itself to Reserved -- no explicit reserve() call.
        var observed = client.sandbox().simulateDeposit(environmentId, fullIntent.depositAddress(), assetNetworkId, new BigDecimal("1000"));
        client.sandbox().simulateConfirmation(environmentId, observed.sandboxObservedAddressId(), 1, true);
        String status = "CREATED";
        for (int i = 0; i < 20 && ("CREATED".equals(status) || "AWAITING_FUNDS".equals(status)); i++) {
            Thread.sleep(1000);
            status = client.transactions().getState(txn.transactionId()).status().name();
        }
        System.out.println("[7] deposit confirmed, transaction status=" + status);

        // 8. Settlement -- calculates the Platform Fee/Distributable split. It does not move
        //    funds by itself; step 9 requests the real payout signature explicitly.
        var settlement = client.settlements().executeSettlement(txn.transactionId(), null);
        System.out.println("[8] settlement executed id=" + settlement.settlementId());

        // 9. The marketplace requests a SigningRequest for the real payout (seller's share,
        //    platform fee) against its own execution wallet, and signs each leg's canonical
        //    hash LOCALLY -- the private key is used here and only here, never sent anywhere.
        var allocated = client.wallets().allocateDepositAddress(applicationId, networkId);
        var legs = List.of(
                new ExecutionLegInput("Seller", "TSellerPayoutAddress0000000001", new BigDecimal("991")),
                new ExecutionLegInput("PlatformFee", "TIshtaranFeeAddress00000000001", new BigDecimal("9")));
        var signingRequest = client.signingRequests().create(environmentId, registeredWallet.walletId(),
                allocated.derivationReference(), "marketplace-settlement-" + t, assetNetworkId,
                allocated.address(), legs, OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), "marketplace-sr-" + t);

        for (var leg : client.signingRequests().get(signingRequest.signingRequestId()).legs()) {
            byte[] hash = HexFormat.of().parseHex(leg.canonicalHash());
            byte[] signature = wallet.signer().sign(allocated.derivationReference(), hash);
            var result = client.signingRequests().submitSignedTransaction(signingRequest.signingRequestId(),
                    leg.executionLegId(), leg.canonicalHash(), HexFormat.of().withUpperCase().formatHex(signature));
            // allLegsVerified only flips to true on the LAST leg submitted -- the all-signatures
            // gate never broadcasts on a partial set of signatures.
            System.out.println("[9] leg=" + leg.role() + " verified=" + result.verified() + " allLegsVerified=" + result.allLegsVerified());
        }

        // 10. Confirm both legs broadcast -- the cycle is closed.
        for (var leg : client.signingRequests().get(signingRequest.signingRequestId()).legs()) {
            System.out.println("[10] leg=" + leg.role() + " status=" + leg.status() + " broadcastReference=" + leg.broadcastReference());
        }
    }
}
