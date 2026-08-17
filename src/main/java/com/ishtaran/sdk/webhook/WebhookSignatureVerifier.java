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
 * Verificação real de assinatura de webhook — algoritmo extraído byte a byte de
 * {@code WebhookSignatureCalculator.cs}/{@code HttpWebhookDeliveryPort.cs} (ver
 * SDK_CAPABILITY_SPEC.md §10): {@code signedContent = "{unixTimestamp}.{rawBodyJson}"},
 * {@code signature = lowercase_hex(HMAC_SHA256(secret, signedContent))}. Usa o {@code rawBody}
 * exatamente como recebido — nunca reserializa o JSON antes de calcular (reserialização pode mudar
 * espaçamento/ordem e quebrar a comparação).
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

    /** Exposto para quem quer calcular sem também validar o timestamp (ex.: testes). */
    public static String compute(long unixTimestampSeconds, String rawBody, String endpointSecret) {
        String signedContent = unixTimestampSeconds + "." + rawBody;
        try {
            var mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(endpointSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] raw = mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA256 indisponível na JVM", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
