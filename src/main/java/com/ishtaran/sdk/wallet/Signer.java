package com.ishtaran.sdk.wallet;

/**
 * Pluggable key storage/signing abstraction — the integrator can bring their own implementation
 * (Vault, KMS, HSM, Secret Manager, Keychain, Android Keystore...) without Ishtaran ever seeing
 * the secret (INV-SC-01). {@link InMemorySigner} is the only implementation shipped by this SDK,
 * explicitly documented as dev/test only — never intended for a real Production integration.
 */
public interface Signer {

    /**
     * Signs {@code canonicalHash} (32 raw bytes, SHA-256 — see {@link com.ishtaran.sdk.signing.CanonicalHash})
     * with the private key derived at {@code derivationIndex} under this wallet's account-level
     * key ({@code .derive(0).derive(index)}, non-hardened — same path the backend uses to derive
     * the corresponding public key). Returns a DER-encoded ECDSA/secp256k1 signature.
     */
    byte[] sign(long derivationIndex, byte[] canonicalHash);
}
