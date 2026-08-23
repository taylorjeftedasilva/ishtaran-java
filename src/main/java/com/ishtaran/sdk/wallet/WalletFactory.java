package com.ishtaran.sdk.wallet;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * SDK Self-Custody Capability (SPEC-018 equivalent) -- local {@link Wallet} generation/import,
 * BIP39 (mnemonic) + BIP32 (extended key) + BIP44 (path {@code m/44'/195'/0'}, TRON
 * {@code coin_type} = 195, brief section 10). The private key never leaves this process
 * (INV-SC-01) -- only the public material (xpub) leaves here, via {@link GeneratedWallet#wallet()}.
 */
public final class WalletFactory {

    /** 256 bits of entropy -> 24-word mnemonic (stronger default than the 128-bit/12-word minimum). */
    private static final int ENTROPY_BITS = 256;

    private static final ChildNumber PURPOSE_44H = new ChildNumber(44, true);
    private static final ChildNumber TRON_COIN_TYPE_195H = new ChildNumber(195, true);
    private static final ChildNumber ACCOUNT_0H = new ChildNumber(0, true);

    private WalletFactory() {
    }

    /** Generates a brand-new wallet — fresh entropy from {@link SecureRandom}, account 0, no BIP39 passphrase (25th word) by default. */
    public static GeneratedWallet generate() {
        var entropy = new byte[ENTROPY_BITS / 8];
        new SecureRandom().nextBytes(entropy);

        var words = MnemonicCode.INSTANCE.toMnemonic(entropy);
        var generated = fromWords(words, "");
        Arrays.fill(entropy, (byte) 0);
        return generated;
    }

    /** Restores a wallet from an existing mnemonic (recovery flow, SPEC-018 brief §9). */
    public static GeneratedWallet restore(String mnemonic, String passphrase) {
        var words = List.of(mnemonic.trim().split("\\s+"));
        return fromWords(words, passphrase == null ? "" : passphrase);
    }

    private static GeneratedWallet fromWords(List<String> words, String passphrase) {
        try {
            MnemonicCode.INSTANCE.check(words); // fail-closed on an invalid checksum/wordlist -- never derives from a malformed mnemonic.
        } catch (MnemonicException e) {
            throw new IllegalArgumentException("Invalid BIP39 mnemonic.", e);
        }

        var seed = MnemonicCode.toSeed(words, passphrase);
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        Arrays.fill(seed, (byte) 0);

        DeterministicKey purpose = HDKeyDerivation.deriveChildKey(master, PURPOSE_44H);
        DeterministicKey coinType = HDKeyDerivation.deriveChildKey(purpose, TRON_COIN_TYPE_195H);
        DeterministicKey account = HDKeyDerivation.deriveChildKey(coinType, ACCOUNT_0H);

        var signer = InMemorySigner.fromAccountKey(account);
        var wallet = new Wallet(DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, signer.accountExtendedPublicKey());
        var mnemonic = String.join(" ", words);

        return new GeneratedWallet(wallet, signer, mnemonic);
    }
}
