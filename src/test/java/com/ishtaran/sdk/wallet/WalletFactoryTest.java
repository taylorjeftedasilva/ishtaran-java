package com.ishtaran.sdk.wallet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletFactoryTest {

    @Test
    void generate_producesA24WordMnemonic_andAWalletWithMatchingPublicMaterial() {
        var generated = WalletFactory.generate();

        assertEquals(24, generated.mnemonic().split("\\s+").length);
        assertEquals(DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, generated.wallet().scheme());
        assertEquals(generated.wallet().accountExtendedPublicKey(), generated.signer().accountExtendedPublicKey());
        assertTrue(generated.wallet().accountExtendedPublicKey().startsWith("xpub"));
    }

    @Test
    void generate_twice_neverProducesTheSameMnemonic() {
        var first = WalletFactory.generate();
        var second = WalletFactory.generate();

        assertNotEquals(first.mnemonic(), second.mnemonic());
        assertNotEquals(first.wallet().accountExtendedPublicKey(), second.wallet().accountExtendedPublicKey());
    }

    @Test
    void restore_withTheSameMnemonic_reproducesTheSameAccountExtendedPublicKey() {
        var original = WalletFactory.generate();

        var restored = WalletFactory.restore(original.mnemonic(), "");

        assertEquals(original.wallet().accountExtendedPublicKey(), restored.wallet().accountExtendedPublicKey());
    }

    @Test
    void restore_withDifferentPassphrase_producesADifferentWallet() {
        var original = WalletFactory.generate();

        var restoredWithPassphrase = WalletFactory.restore(original.mnemonic(), "extra-security-word");

        assertNotEquals(original.wallet().accountExtendedPublicKey(), restoredWithPassphrase.wallet().accountExtendedPublicKey());
    }

    @Test
    void restore_withInvalidMnemonic_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> WalletFactory.restore("not a valid bip39 mnemonic at all", ""));
    }

    @Test
    void restore_withValidWordsButInvalidChecksum_throwsIllegalArgumentException() {
        // 12 real words from the BIP39 wordlist, but in an order that breaks the checksum (BR-WLT-003 client-side equivalent — never derives from corrupted material).
        var bogusChecksum = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon";

        assertThrows(IllegalArgumentException.class, () -> WalletFactory.restore(bogusChecksum, ""));
    }
}
