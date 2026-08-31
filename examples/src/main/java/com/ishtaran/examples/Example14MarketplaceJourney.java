package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;
import com.ishtaran.sdk.model.dataplane.SettlementResponse;
import com.ishtaran.sdk.model.enums.DerivationScheme;
import com.ishtaran.sdk.wallet.TronAddress;
import com.ishtaran.sdk.wallet.WalletFactory;

import java.math.BigDecimal;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * 14 — End-to-end marketplace journey, re-verified live 2026-08-31 against the Network Execution
 * Engine: a buyer pays into a marketplace that holds its own self-custody execution wallet, a
 * seller signs up as their own {@code AccountHolder} to receive the payout, and the marketplace
 * signs the real payout itself — Ishtaran never sees a private key. Closes the full cycle other
 * examples cover individually (self-service signup, self-custody signing, Payment Intents,
 * AccountHolder invitations): this one connects them into one story, the way a real integrator
 * would use them.
 *
 * <p>Real gaps found and fixed while building/re-validating this example, not hypothetical:
 * <ul>
 *   <li>{@code accounts().authorizeApplication(...)} requires a <b>Member</b> session — it always
 *       rejects an API Key, even though {@code Accounts} is otherwise usable with either (see
 *       {@code AccountsEndpoints.cs}, {@code MemberPermissionPolicy.Require}).</li>
 *   <li>Once a Payment Intent's deposit is confirmed, the Transaction moves itself to
 *       {@code Reserved} — no explicit {@code transactions().reserve(...)} call is needed (or
 *       valid) in this path.</li>
 *   <li>{@code executeSettlement()} now builds its OWN {@code SigningRequest} automatically
 *       (confirmed live 2026-08-31) — an earlier version of this example manually called
 *       {@code signingRequests().create(...)} with hand-picked destination addresses right after
 *       {@code executeSettlement()}, which built a second, unrelated SigningRequest disconnected
 *       from the real Settlement. That is now wrong: sign the SigningRequest
 *       {@code executeSettlement()} itself returns ({@code settlement.signingRequestId()}).</li>
 *   <li>Under SelfCustody, broadcasting a beneficiary's leg costs real network resources, charged
 *       separately from the Platform Fee — a {@code NetworkCostPayerAccount} must be registered
 *       once per (organizationId, assetNetworkId) before the first real Settlement, or
 *       {@code executeSettlement()} fails with 422
 *       {@code PAYOUT_BATCH_NETWORK_COST_PAYER_ACCOUNT_NOT_REGISTERED}. This example registers
 *       the marketplace's own commission Account as the payer — a real business decision, not a
 *       technical afterthought.</li>
 *   <li>Each beneficiary paid under SelfCustody (the seller, and the platform's own commission)
 *       needs a registered {@code ExecutionDestination} — the real on-chain address that
 *       beneficiary actually receives funds at — before {@code executeSettlement()} can build a
 *       leg for them.</li>
 * </ul>
 *
 * <p><b>Known gap, not fixed here:</b> {@code TransactionsResource.create(...)} in this SDK has no
 * {@code environmentId} parameter at all (unlike the TypeScript SDK's equivalent) — the backend
 * does not hard-reject a Transaction created this way, but it is semantically incomplete. Fixing
 * it is out of scope for this example; see {@code SDK_CAPABILITY_SPEC.md} item 10.
 *
 * <p>Requires only {@code ISHTARAN_ASSET_NETWORK_ID}/{@code ISHTARAN_NETWORK_ID} env vars (an
 * Asset Network already seeded in the target Sandbox) — everything else (Organization,
 * Application, Environment, API Key, all three Accounts) is provisioned by the example itself.
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
        //    ever reaches Ishtaran. This is the wallet that will sign the real payout in step 10,
        //    and that allocates the marketplace's own commission address in step 6.
        var generatedWallet = WalletFactory.generate();
        var registeredWallet = client.wallets().register(applicationId, networkId,
                DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, generatedWallet.wallet().accountExtendedPublicKey(),
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
        //    for a one-off payer). The marketplace's own commission Account, same shape.
        UUID buyerAccountId = client.accounts().create(organizationId, "buyer-" + t).accountId();
        UUID marketplaceRevenueAccountId = client.accounts().create(organizationId, "marketplace-revenue-" + t).accountId();
        System.out.println("[4] buyer accountId=" + buyerAccountId + " marketplaceRevenueAccountId=" + marketplaceRevenueAccountId);

        // 5. Authorize all three Accounts for this Application. GOTCHA: this call requires the
        //    Member session (`owner`), not the API Key client (`client`) -- see class doc.
        owner.accounts().authorizeApplication(organizationId, sellerAccountId, applicationId);
        owner.accounts().authorizeApplication(organizationId, buyerAccountId, applicationId);
        owner.accounts().authorizeApplication(organizationId, marketplaceRevenueAccountId, applicationId);
        System.out.println("[5] all three accounts authorized for the application");

        // 6. Register where each SelfCustody beneficiary actually gets paid, and who pays for
        //    network execution. The seller's destination is their OWN external wallet (a
        //    throwaway wallet here stands in for "whatever wallet the seller really uses" --
        //    Ishtaran never touches its key). The marketplace's own commission lands on an
        //    address of its OWN execution wallet -- and that same commission Account is the one
        //    registered to pay real network cost, out of its own commission, a real business
        //    decision.
        var sellerWallet = WalletFactory.generate();
        String sellerDestinationAddress = TronAddress.derive(sellerWallet.wallet().accountExtendedPublicKey(), 0);
        client.executionDestinations().register(organizationId, sellerAccountId, assetNetworkId, sellerDestinationAddress);
        var marketplaceRevenueAllocation = client.wallets().allocateDepositAddress(applicationId, networkId);
        client.executionDestinations().register(organizationId, marketplaceRevenueAccountId, assetNetworkId, marketplaceRevenueAllocation.address());
        client.networkCostPayerAccounts().register(organizationId, assetNetworkId, marketplaceRevenueAccountId);
        System.out.println("[6] ExecutionDestinations + NetworkCostPayerAccount registered");

        // 7. Transaction + Payment Intent. An explicit Split is required here (2 non-payer
        //    Participants -- seller and marketplace -- BR-SPL-004/BR-SPL-003: a single implicit
        //    100% only applies with exactly one beneficiary).
        var payer = new ParticipantInput(buyerAccountId, "payer", true, null);
        var seller = new ParticipantInput(sellerAccountId, "seller", false, new BigDecimal("90"));
        var marketplace = new ParticipantInput(marketplaceRevenueAccountId, "marketplace", false, new BigDecimal("10"));
        var txn = client.transactions().create(organizationId, applicationId, null, assetNetworkId,
                new BigDecimal("1000"), List.of(payer, seller, marketplace), "marketplace-txn-" + t);
        var intent = client.deposits().createPaymentIntent(organizationId, txn.transactionId(), assetNetworkId, new BigDecimal("1000"), null, null);
        var fullIntent = client.deposits().getPaymentIntent(intent.paymentIntentId());
        System.out.println("[7] paymentIntentId=" + intent.paymentIntentId() + " depositAddress=" + fullIntent.depositAddress());

        // 8. Simulate the buyer's on-chain deposit and its confirmation (Sandbox only). Once
        //    confirmed, the Transaction moves itself to Reserved -- no explicit reserve() call.
        var observed = client.sandbox().simulateDeposit(environmentId, fullIntent.depositAddress(), assetNetworkId, new BigDecimal("1000"));
        client.sandbox().simulateConfirmation(environmentId, observed.sandboxObservedAddressId(), 1, true);
        String status = "CREATED";
        for (int i = 0; i < 20 && ("CREATED".equals(status) || "AWAITING_FUNDS".equals(status)); i++) {
            Thread.sleep(1000);
            status = client.transactions().getState(txn.transactionId()).status().name();
        }
        System.out.println("[8] deposit confirmed, transaction status=" + status);

        // 9. Settlement -- calculates the Platform Fee/Split AND builds a real SigningRequest
        //    itself (SelfCustody, confirmed live): one ExecutionLeg per beneficiary (seller,
        //    marketplace commission), each addressed via the ExecutionDestination registered in
        //    step 6. Nothing is final yet -- signingRequestId is populated, but no Ledger Entry
        //    exists until every leg confirms (step 10-11).
        var executed = client.settlements().executeSettlement(txn.transactionId(), null, null);
        SettlementResponse settlement = client.settlements().get(executed.settlementId());
        System.out.println("[9] settlement executed id=" + settlement.settlementId() + " signingRequestId=" + settlement.signingRequestId());

        // 10. Sign every leg of THAT SAME SigningRequest, locally, with the marketplace's own
        //     execution wallet -- the private key is used here and only here, never sent
        //     anywhere. A Settlement with nothing to execute on-chain (every allocation Retained,
        //     Fee zero) has signingRequestId=null; this example's Split always produces real legs
        //     to sign.
        if (settlement.signingRequestId() == null) throw new IllegalStateException("Expected a real SigningRequest for this Settlement");
        UUID signingRequestId = settlement.signingRequestId();
        var signingRequest = client.signingRequests().get(signingRequestId);
        for (var leg : signingRequest.legs()) {
            byte[] hashBytes = HexFormat.of().parseHex(leg.canonicalHash());
            byte[] signature = generatedWallet.signer().sign(signingRequest.derivationReference(), hashBytes);
            var result = client.signingRequests().submitSignedTransaction(signingRequestId, leg.executionLegId(),
                    leg.canonicalHash(), HexFormat.of().withUpperCase().formatHex(signature));
            System.out.println("[10] leg=" + leg.role() + " verified=" + result.verified() + " allLegsVerified=" + result.allLegsVerified());
        }

        // 11. Simulate each leg's on-chain confirmation (Sandbox only) and wait for the
        //     Settlement to reach Completed -- only then does the Ledger reflect anything
        //     (Delivered, never Available, since both beneficiaries' ExecutionDestinations are
        //     external wallets -- see concepts/self-custody and
        //     concepts/transactions-settlements on the docs site).
        for (int i = 0; i < 20; i++) {
            var current = client.signingRequests().get(signingRequestId);
            boolean allReferenced = current.legs().stream().allMatch(leg -> leg.broadcastReference() != null);
            if (allReferenced) {
                for (var leg : current.legs()) {
                    UUID broadcastAttemptId = sandboxBroadcastAttemptIdFromReference(leg.broadcastReference());
                    client.sandbox().simulateBroadcastConfirmation(environmentId, broadcastAttemptId, 1, true);
                }
                break;
            }
            Thread.sleep(500);
        }

        var finalSettlement = client.settlements().get(settlement.settlementId());
        for (int i = 0; i < 30 && !"COMPLETED".equals(finalSettlement.status().name()); i++) {
            Thread.sleep(500);
            finalSettlement = client.settlements().get(settlement.settlementId());
        }
        System.out.println("[11] settlement status=" + finalSettlement.status().name());

        var sellerPayable = client.payout().getPayableSummary(sellerAccountId, assetNetworkId);
        var marketplacePayable = client.payout().getPayableSummary(marketplaceRevenueAccountId, assetNetworkId);
        System.out.println("[11] seller paid=" + sellerPayable.paid() + " marketplace paid=" + marketplacePayable.paid());
    }

    private static UUID sandboxBroadcastAttemptIdFromReference(String reference) {
        String prefix = "sandbox-broadcast-";
        if (!reference.startsWith(prefix)) throw new IllegalStateException("Unexpected broadcastReference format: " + reference);
        String hexN = reference.substring(prefix.length());
        return UUID.fromString(hexN.substring(0, 8) + "-" + hexN.substring(8, 12) + "-" + hexN.substring(12, 16)
                + "-" + hexN.substring(16, 20) + "-" + hexN.substring(20, 32));
    }
}
