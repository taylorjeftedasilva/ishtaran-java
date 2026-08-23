package com.ishtaran.sdk.wallet;

/**
 * Mirrors {@code ExecutionCustody.Domain.Enums.DerivationScheme} (backend, SPEC-018) — only
 * {@link #TRON_BIP44_HARDENED_ACCOUNT} has a real implementation on either side. The account-level
 * material is derived at the hardened path {@code m/44'/195'/account'} (TRON coin_type = 195);
 * the two remaining levels ({@code change}, fixed at 0, and {@code index}) are derived
 * non-hardened, both locally by this SDK (private key) and remotely by the backend (public key
 * only, CKDpub) from the account-level extended public key registered via
 * {@code RegisterWalletCommand}.
 */
public enum DerivationScheme {
    TRON_BIP44_HARDENED_ACCOUNT
}
