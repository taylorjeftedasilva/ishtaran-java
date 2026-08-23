package com.ishtaran.sdk.wallet;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySignerTest {

    private static byte[] sha256(String value) throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(value.getBytes());
    }

    /** Derives the same address-level PUBLIC key {@link InMemorySigner#sign} used PRIVATELY, purely from the xpub — proves signature verification never needs the private key (INV-SC-01, backend-side verification path). */
    private static ECKey addressPublicKeyFor(String accountExtendedPublicKey, long index) {
        DeterministicKey accountKey = DeterministicKey.deserializeB58(accountExtendedPublicKey, MainNetParams.get());
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(0, false));
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) index, false));
        return ECKey.fromPublicOnly(addressKey.getPubKey());
    }

    @Test
    void sign_producesASignatureThatVerifiesAgainstTheCorrespondingPublicKey() throws Exception {
        var generated = WalletFactory.generate();
        var hash = sha256("canonical-hash-placeholder");

        var derSignature = generated.signer().sign(3, hash);

        var signature = ECKey.ECDSASignature.decodeFromDER(derSignature);
        var publicKey = addressPublicKeyFor(generated.wallet().accountExtendedPublicKey(), 3);
        assertTrue(ECKey.verify(hash, signature, publicKey.getPubKey()));
    }

    @Test
    void sign_signatureDoesNotVerifyAgainstADifferentDerivationIndex() throws Exception {
        var generated = WalletFactory.generate();
        var hash = sha256("canonical-hash-placeholder");

        var derSignature = generated.signer().sign(3, hash);

        var signature = ECKey.ECDSASignature.decodeFromDER(derSignature);
        var wrongPublicKey = addressPublicKeyFor(generated.wallet().accountExtendedPublicKey(), 4);
        assertFalse(ECKey.verify(hash, signature, wrongPublicKey.getPubKey()));
    }

    @Test
    void sign_signatureDoesNotVerifyAgainstATamperedHash() throws Exception {
        var generated = WalletFactory.generate();
        var hash = sha256("canonical-hash-placeholder");
        var tamperedHash = sha256("canonical-hash-placeholder-tampered");

        var derSignature = generated.signer().sign(3, hash);

        var signature = ECKey.ECDSASignature.decodeFromDER(derSignature);
        var publicKey = addressPublicKeyFor(generated.wallet().accountExtendedPublicKey(), 3);
        assertFalse(ECKey.verify(tamperedHash, signature, publicKey.getPubKey()));
    }

    @Test
    void sign_rejectsAHashThatIsNot32Bytes() {
        var generated = WalletFactory.generate();

        assertThrows(IllegalArgumentException.class, () -> generated.signer().sign(0, new byte[]{1, 2, 3}));
    }

    @Test
    void sign_rejectsANegativeDerivationIndex() throws Exception {
        var generated = WalletFactory.generate();
        var hash = sha256("canonical-hash-placeholder");

        assertThrows(IllegalArgumentException.class, () -> generated.signer().sign(-1, hash));
    }

    @Test
    void accountExtendedPublicKey_neverContainsPrivateKeyMaterial() {
        var generated = WalletFactory.generate();

        // Base58Check "xpub" (public) prefix — never "xprv" (private). Regression guard for INV-SC-01.
        assertTrue(generated.wallet().accountExtendedPublicKey().startsWith("xpub"));
        assertFalse(generated.wallet().accountExtendedPublicKey().contains("xprv"));
    }
}
