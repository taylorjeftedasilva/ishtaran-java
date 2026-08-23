package com.ishtaran.examples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

import java.time.Instant;

/**
 * 10 — Webhook signature verification. The only example that is 100% runnable without a real
 * API running (local computation, no HTTP call) — it simulates a real platform delivery to
 * demonstrate the full protocol, including the tampered-payload rejection case.
 */
public final class Example10WebhookVerification {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .apiKey("example-key-not-a-real-network-call")
                .environment(Environment.LOCAL)
                .build();

        String endpointSecret = "whsec_example_secret_do_not_use_in_production";
        String rawBody = "{\"eventType\":\"payment.received\",\"amount\":100}";
        long timestamp = Instant.now().getEpochSecond();

        // On the platform side: signature computed and sent in the X-Webhook-Signature/
        // X-Webhook-Timestamp headers together with the rawBody as the real HTTP delivery body.
        String signature = WebhookSignatureVerifier.compute(timestamp, rawBody, endpointSecret);
        System.out.println("Computed signature (simulating the platform): " + signature);

        // On the integrator side: real verification using the SDK, with no network call.
        boolean valid = client.verifyWebhookSignature(rawBody, signature, String.valueOf(timestamp), endpointSecret);
        System.out.println("Signature valid? " + valid);

        // Payload tampered with after sending -- verification must reject it.
        String tamperedBody = "{\"eventType\":\"payment.received\",\"amount\":999999}";
        boolean tamperedValid = client.verifyWebhookSignature(tamperedBody, signature, String.valueOf(timestamp), endpointSecret);
        System.out.println("Tampered payload still valid? " + tamperedValid + " (expected: false)");
    }
}
