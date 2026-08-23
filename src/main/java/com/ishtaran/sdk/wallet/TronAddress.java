package com.ishtaran.sdk.wallet;

import org.bitcoinj.base.Base58;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.crypto.digests.KeccakDigest;

import java.util.Arrays;

/**
 * TRON mainnet address derivation from a PUBLIC account-level extended key — CKDpub only (BIP32
 * non-hardened child derivation), never needs or touches a private key (INV-SC-01). Mirrors
 * {@code TronAddressDerivationProvider} (backend, ExecutionCustody.Infrastructure, TDR-017) field
 * for field — used here by the SDK to independently verify the {@code sourceAddress}/
 * {@code destinationAddress} values it receives in a {@code SigningRequest} before signing
 * (defense in depth, SPEC-019/020 brief §28: "SDK rejects: wrong derivation").
 * <p>
 * Algorithm: secp256k1 public key, uncompressed (65 bytes: 0x04 || X(32) || Y(32)) -> drop the
 * 0x04 prefix -> Keccak-256 (64-byte input) -> last 20 bytes -> prepend mainnet prefix 0x41 ->
 * Base58Check (same checksum construction as Bitcoin, different version byte/payload).
 */
public final class TronAddress {

    private static final int MAINNET_PREFIX = 0x41;

    private TronAddress() {
    }

    /** Derives the address at {@code .derive(0).derive(index)} (change fixed at 0, index variable) under the given account-level xpub — path {@code m/44'/195'/0'/0/<index>} (brief §10). */
    public static String derive(String accountExtendedPublicKey, long index) {
        if (index < 0 || index > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("index out of range for a non-hardened BIP32 child index.");
        }

        DeterministicKey accountKey = DeterministicKey.deserializeB58(accountExtendedPublicKey, MainNetParams.get());
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(0, false));
        DeterministicKey addressKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) index, false));

        byte[] uncompressed = addressKey.decompress().getPubKey(); // 65 bytes: 0x04 || X || Y
        byte[] hashInput = Arrays.copyOfRange(uncompressed, 1, uncompressed.length); // remove 0x04 -> 64 bytes

        byte[] hash = keccak256(hashInput);
        byte[] last20 = Arrays.copyOfRange(hash, 12, 32);

        return Base58.encodeChecked(MAINNET_PREFIX, last20);
    }

    private static byte[] keccak256(byte[] input) {
        var digest = new KeccakDigest(256);
        digest.update(input, 0, input.length);
        var output = new byte[32];
        digest.doFinal(output, 0);
        return output;
    }
}
