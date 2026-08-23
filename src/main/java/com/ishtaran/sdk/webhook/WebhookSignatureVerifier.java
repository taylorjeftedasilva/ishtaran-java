package com.ishtaran.sdk.webhook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Real webhook signature verification -- algorithm extracted byte-for-byte from
 * {@code WebhookSignatureCalculator.cs}/{@code HttpWebhookDeliveryPort.cs} (see
 * SDK_CAPABILITY_SPEC.md section 10): {@code signedContent = "{unixTimestamp}.{rawBodyJson}"},
 * {@code signature = lowercase_hex(HMAC_SHA256(secret, signedContent))}. Uses {@code rawBody}
 * exactly as received -- never re-serializes the JSON before computing (re-serialization could
 * change spacing/order and break the comparison).
 */
public final class WebhookSignatureVerifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Duration DEFAULT_TOLERANCE = Duration.ofMinutes(5);

    private WebhookSignatureVerifier() {
    }

    public static boolean verify(String rawBody, String signatureHeader, String timestampHeader, String endpointSecret) {
        return verify(rawBody, signatureHeader, timestampHeader, endpointSecret, DEFAULT_TOLERANCE);
    }

    public static boolean verify(String rawBody, String signatureHeader, String timestampHeader,
                                  String endpointSecret, Duration tolerance) {
        if (rawBody == null || signatureHeader == null || timestampHeader == null
                || endpointSecret == null || endpointSecret.isBlank()) {
            return false;
        }

        long timestampSeconds;
        try {
            timestampSeconds = Long.parseLong(timestampHeader.trim());
        } catch (NumberFormatException e) {
            return false;
        }

        var age = Duration.between(Instant.ofEpochSecond(timestampSeconds), Instant.now()).abs();
        if (age.compareTo(tolerance) > 0) {
            return false;
        }

        String expectedSignature = compute(timestampSeconds, rawBody, endpointSecret);
        return constantTimeEquals(expectedSignature, signatureHeader.trim().toLowerCase(java.util.Locale.ROOT));
    }

    /** Exposed for callers who want to compute without also validating the timestamp (e.g. tests). */
    public static String compute(long unixTimestampSeconds, String rawBody, String endpointSecret) {
        String signedContent = unixTimestampSeconds + "." + rawBody;
        try {
            var mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(endpointSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] raw = mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA256 unavailable on the JVM", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
