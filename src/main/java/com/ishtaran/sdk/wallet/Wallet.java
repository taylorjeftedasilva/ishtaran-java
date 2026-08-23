package com.ishtaran.sdk.wallet;

import java.util.Objects;

/**
 * PUBLIC metadata of a self-custody wallet — never contains a private key, seed, or mnemonic
 * (INV-SC-01). {@link #accountExtendedPublicKey()} is the account-level extended public key
 * (BIP32 xpub, standard Base58Check serialization) — the only material registered with Ishtaran
 * via {@code RegisterWalletCommand} (backend, SPEC-018). Classified Confidential, not Secret: it
 * cannot move funds by itself, but it can expose the client's address tree/correlation — never
 * log it, never send it anywhere other than the official wallet registration call.
 */
public final class Wallet {

    private final DerivationScheme scheme;
    private final String accountExtendedPublicKey;

    public Wallet(DerivationScheme scheme, String accountExtendedPublicKey) {
        this.scheme = Objects.requireNonNull(scheme, "scheme must not be null");
        this.accountExtendedPublicKey = Objects.requireNonNull(accountExtendedPublicKey, "accountExtendedPublicKey must not be null");
    }

    public DerivationScheme scheme() {
        return scheme;
    }

    /** The public derivation material to register with the API — never a private key (INV-SC-01). */
    public String accountExtendedPublicKey() {
        return accountExtendedPublicKey;
    }
}
