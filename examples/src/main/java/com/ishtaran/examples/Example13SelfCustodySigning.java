package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.model.dataplane.ExecutionLegInput;
import com.ishtaran.sdk.wallet.WalletFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * 13 — End-to-end self-custody wallet/signing (SPEC-017-021, checkpoint 7/8): generates the
 * wallet LOCALLY ({@link WalletFactory}), registers only the public key, allocates a real
 * deposit address, creates a 2-Leg {@code SigningRequest}, signs each canonical hash returned
 * by the API with the private key (which NEVER leaves this process — INV-SC-01), and submits it
 * back. Proves the all-signatures gate (brief §11): {@code allLegsVerified} only becomes
 * {@code true} after the second signature, and both Legs only become {@code Broadcast} at that
 * same instant.
 */
public final class Example13SelfCustodySigning {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey(System.getenv("ISHTARAN_API_KEY"))
                .environment(Environment.LOCAL)
                .baseUrl(System.getenv().getOrDefault("ISHTARAN_BASE_URL", "http://localhost:8080"))
                .build();

        UUID applicationId = UUID.fromString(System.getenv("ISHTARAN_APPLICATION_ID"));
        UUID environmentId = UUID.fromString(System.getenv("ISHTARAN_SANDBOX_ENVIRONMENT_ID"));
        UUID networkId = UUID.fromString(System.getenv("ISHTARAN_NETWORK_ID"));
        UUID assetNetworkId = UUID.fromString(System.getenv("ISHTARAN_ASSET_NETWORK_ID"));

        // 1. Wallet generated locally — mnemonic/private key never leave this process.
        var wallet = WalletFactory.generate();
        System.out.println("mnemonic (backup — NEVER sent to the API): " + wallet.mnemonic());
        System.out.println("accountExtendedPublicKey (only this goes to the API): " + wallet.wallet().accountExtendedPublicKey());

        // 2. Register the wallet — the API only receives the public key.
        var registered = client.wallets().register(applicationId, networkId, com.ishtaran.sdk.model.enums.DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT,
                wallet.wallet().accountExtendedPublicKey(), "example13-wallet-" + UUID.randomUUID());
        System.out.println("walletId=" + registered.walletId());

        // 3. GetWallet never includes the key material (BR-WLT-002) — local confirmation, no formal assert.
        var fetched = client.wallets().get(registered.walletId());
        System.out.println("wallet.scheme=" + fetched.scheme() + " nextDerivationIndex=" + fetched.nextDerivationIndex());

        // 4. Allocate a real deposit address — derived from the registered xpub.
        var allocated = client.wallets().allocateDepositAddress(applicationId, networkId);
        System.out.println("sourceAddress=" + allocated.address() + " derivationReference=" + allocated.derivationReference());

        // 5. Create the SigningRequest — 2 legs (Seller + Ishtaran Platform Fee), amounts already
        //    calculated by the caller (real Settlement/Withdrawals integration is future work).
        var legs = List.of(
                new ExecutionLegInput("Seller", "TSellerDestinationAddress123456", new BigDecimal("90")),
                new ExecutionLegInput("PlatformFee", "TIshtaranFeeDestinationAddr123", new BigDecimal("1")));
        // expiresAt always in explicit UTC — never the process's local timezone (avoids ambiguity
        // for anyone reading the request body; the API normalizes any offset, but a well-behaved
        // SDK never relies on that).
        var created = client.signingRequests().create(environmentId, registered.walletId(), allocated.derivationReference(),
                "example13-settlement-" + UUID.randomUUID(), assetNetworkId, allocated.address(), legs,
                OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), "example13-signing-request-" + UUID.randomUUID());
        System.out.println("signingRequestId=" + created.signingRequestId());

        // 6. Fetch the SigningRequest — each Leg already carries the canonicalHash computed by the backend.
        var signingRequest = client.signingRequests().get(created.signingRequestId());

        // 7. Sign each hash locally and submit — never in parallel, to observe the
        //    all-signatures gate: the 1st submission must never trigger a broadcast on its own.
        for (var leg : signingRequest.legs()) {
            byte[] canonicalHash = HexFormat.of().parseHex(leg.canonicalHash());
            byte[] signature = wallet.signer().sign(allocated.derivationReference(), canonicalHash);
            String signatureHex = HexFormat.of().withUpperCase().formatHex(signature);

            var result = client.signingRequests().submitSignedTransaction(
                    created.signingRequestId(), leg.executionLegId(), leg.canonicalHash(), signatureHex);

            System.out.println("leg=" + leg.role() + " verified=" + result.verified()
                    + " allLegsVerified=" + result.allLegsVerified() + " mismatchReason=" + result.mismatchReason());
        }

        // 8. Confirm the final state — both Legs must be Broadcast, each with a real broadcastReference (Sandbox).
        var finalState = client.signingRequests().get(created.signingRequestId());
        for (var leg : finalState.legs()) {
            System.out.println("leg=" + leg.role() + " status=" + leg.status() + " broadcastReference=" + leg.broadcastReference());
        }
    }
}
