package com.ishtaran.sdk.wallet;

import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.util.Objects;

/**
 * Reference {@link Signer} implementation — holds the account-level private key in plain memory
 * for the lifetime of the process. <b>Documented as insecure for Production</b> (SDK Self-Custody
 * Capability Spec §KeyStore): no encryption at rest, no OS-level key protection. Integrators who
 * need real security must implement {@link Signer} themselves against a Vault/KMS/HSM/Secret
 * Manager/Keychain/Android Keystore — this SDK never mandates a specific backend (SPEC-018 brief
 * §8). The private key never leaves this class (never serialized, never logged, never sent to the
 * Ishtaran API — INV-SC-01); {@link #accountExtendedPublicKey()} exposes only the public material
 * meant for {@code RegisterWalletCommand}.
 */
public final class InMemorySigner implements Signer {

    private final DeterministicKey accountKey;

    private InMemorySigner(DeterministicKey accountKey) {
        this.accountKey = Objects.requireNonNull(accountKey, "accountKey must not be null");
    }

    static InMemorySigner fromAccountKey(DeterministicKey accountKey) {
        return new InMemorySigner(accountKey);
    }

    /** Account-level xpub (Base58Check, mainnet envelope reused purely as a BIP32-standard serialization — see TDR-017 Java equivalent) — the only material to register with the API. */
    public String accountExtendedPublicKey() {
        return accountKey.dropPrivateBytes().dropParent().serializePubB58(org.bitcoinj.params.MainNetParams.get());
    }

    @Override
    public byte[] sign(long derivationIndex, byte[] canonicalHash) {
        if (canonicalHash == null || canonicalHash.length != 32) {
            throw new IllegalArgumentException("canonicalHash must be exactly 32 bytes (SHA-256 digest).");
        }
        if (derivationIndex < 0 || derivationIndex > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("derivationIndex out of range for a non-hardened BIP32 child index.");
        }

        var changeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(0, false));
        var addressKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) derivationIndex, false));

        ECKey.ECDSASignature signature = addressKey.sign(Sha256Hash.wrap(canonicalHash));
        return signature.encodeToDER();
    }
}
