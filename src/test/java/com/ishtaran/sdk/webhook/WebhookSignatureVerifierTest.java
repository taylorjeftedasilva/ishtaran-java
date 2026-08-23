package com.ishtaran.sdk.webhook;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhookSignatureVerifierTest {

    private static final String SECRET = "whsec_test_secret_1234567890";

    @Test
    void verify_realAlgorithm_acceptsCorrectSignature() {
        String body = "{\"event\":\"payment.received\",\"amount\":100}";
        long ts = Instant.now().getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(ts, body, SECRET);

        assertTrue(WebhookSignatureVerifier.verify(body, signature, String.valueOf(ts), SECRET));
    }

    @Test
    void verify_tamperedPayload_rejected() {
        String body = "{\"event\":\"payment.received\",\"amount\":100}";
        long ts = Instant.now().getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(ts, body, SECRET);

        String tamperedBody = "{\"event\":\"payment.received\",\"amount\":999999}";
        assertFalse(WebhookSignatureVerifier.verify(tamperedBody, signature, String.valueOf(ts), SECRET));
    }

    @Test
    void verify_tamperedSignature_rejected() {
        String body = "{\"event\":\"payment.received\",\"amount\":100}";
        long ts = Instant.now().getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(ts, body, SECRET);
        String tamperedSignature = signature.substring(0, signature.length() - 4) + "dead";

        assertFalse(WebhookSignatureVerifier.verify(body, tamperedSignature, String.valueOf(ts), SECRET));
    }

    @Test
    void verify_expiredTimestamp_rejected() {
        String body = "{\"event\":\"payment.received\"}";
        long staleTs = Instant.now().minus(Duration.ofHours(2)).getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(staleTs, body, SECRET);

        assertFalse(WebhookSignatureVerifier.verify(body, signature, String.valueOf(staleTs), SECRET));
    }

    @Test
    void verify_wrongSecret_rejected() {
        String body = "{\"event\":\"payment.received\"}";
        long ts = Instant.now().getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(ts, body, SECRET);

        assertFalse(WebhookSignatureVerifier.verify(body, signature, String.valueOf(ts), "whsec_wrong_secret"));
    }

    @Test
    void verify_caseInsensitiveHexSignature_accepted() {
        String body = "{\"event\":\"x\"}";
        long ts = Instant.now().getEpochSecond();
        String signature = WebhookSignatureVerifier.compute(ts, body, SECRET).toUpperCase(java.util.Locale.ROOT);

        assertTrue(WebhookSignatureVerifier.verify(body, signature, String.valueOf(ts), SECRET));
    }

    @Test
    void compute_matchesKnownVector_forRealAlgorithm() {
        // signedContent = "{timestamp}.{rawBodyJson}"; HMAC-SHA256(secret, signedContent), lowercase hex.
        // Vector computed independently via Python (hmac.new(...).hexdigest()), not by the
        // verifier itself, to catch an implementation bug that a round-trip test
        // (compute -> verify) alone would not catch.
        String result = WebhookSignatureVerifier.compute(1700000000L, "{\"a\":1}", "topsecret");
        assertEquals("6a939b0c71853d606167625a15168ee9188c6a511c773ef4f42d307f3849e50f", result);
    }
}
