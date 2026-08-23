package com.ishtaran.sdk.signing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * SPEC-019 — canonical hash reference implementation for the Java SDK. Must reproduce the backend
 * algorithm ({@code CreateSigningRequestCommandHandler.ComputeCanonicalLegHash}, C#) byte for
 * byte — see {@code docs/specs/execution-custody/CANONICAL-HASH-TEST-VECTORS.md} for the full
 * specification and the reference vectors this class is tested against
 * ({@code CanonicalHashReferenceVectorTest}). Never JSON (key order/whitespace/number formatting
 * vary across languages, the classic source of cross-language hash mismatches) — a fixed
 * pipe-joined string, SHA-256, uppercase hex.
 */
public final class CanonicalHash {

    private CanonicalHash() {
    }

    /**
     * @param amount decimal amount — formatted to exactly 18 fractional digits, invariant/no grouping (matches C# {@code "F18"}/{@code CultureInfo.InvariantCulture}).
     * @param expiresAt expiration instant — formatted as whole Unix seconds (never ISO-8601, eliminates timezone/format ambiguity across languages).
     * @return uppercase hex SHA-256 digest of the canonical pipe-joined representation.
     */
    public static String compute(
            int protocolVersion, UUID environmentId, UUID walletId, long derivationReference, String originReference,
            UUID assetNetworkId, String sourceAddress, String legRole, String destinationAddress, BigDecimal amount,
            Instant expiresAt) {

        var normalized = String.join("|",
                Integer.toString(protocolVersion),
                environmentId.toString(),
                walletId.toString(),
                Long.toString(derivationReference),
                originReference,
                assetNetworkId.toString(),
                sourceAddress,
                legRole,
                destinationAddress,
                formatAmount(amount),
                Long.toString(expiresAt.getEpochSecond()));

        return sha256Hex(normalized);
    }

    private static String formatAmount(BigDecimal amount) {
        return amount.setScale(18, RoundingMode.UNNECESSARY).toPlainString();
    }

    private static String sha256Hex(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().withUpperCase().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is guaranteed available on every JDK implementation.", e);
        }
    }
}
