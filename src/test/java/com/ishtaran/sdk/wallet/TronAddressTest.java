package com.ishtaran.sdk.wallet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TronAddressTest {

    @Test
    void derive_producesA34CharacterAddressStartingWithT() {
        var generated = WalletFactory.generate();

        var address = TronAddress.derive(generated.wallet().accountExtendedPublicKey(), 0);

        assertEquals(34, address.length());
        assertTrue(address.startsWith("T"));
    }

    @Test
    void derive_isDeterministic_sameXpubAndIndexAlwaysProduceTheSameAddress() {
        var generated = WalletFactory.generate();
        var xpub = generated.wallet().accountExtendedPublicKey();

        var first = TronAddress.derive(xpub, 7);
        var second = TronAddress.derive(xpub, 7);

        assertEquals(first, second);
    }

    @Test
    void derive_differentIndices_produceDifferentAddresses() {
        var generated = WalletFactory.generate();
        var xpub = generated.wallet().accountExtendedPublicKey();

        var addressAtZero = TronAddress.derive(xpub, 0);
        var addressAtOne = TronAddress.derive(xpub, 1);

        assertNotEquals(addressAtZero, addressAtOne);
    }

    @Test
    void derive_differentWallets_produceDifferentAddressesAtTheSameIndex() {
        var walletA = WalletFactory.generate();
        var walletB = WalletFactory.generate();

        var addressA = TronAddress.derive(walletA.wallet().accountExtendedPublicKey(), 0);
        var addressB = TronAddress.derive(walletB.wallet().accountExtendedPublicKey(), 0);

        assertNotEquals(addressA, addressB);
    }

    @Test
    void derive_rejectsNegativeIndex() {
        var generated = WalletFactory.generate();

        assertThrows(IllegalArgumentException.class,
                () -> TronAddress.derive(generated.wallet().accountExtendedPublicKey(), -1));
    }
}
