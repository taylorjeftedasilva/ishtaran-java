package com.ishtaran.samples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;
import com.ishtaran.sdk.webhook.WebhookSignatureVerifier;

/**
 * Real consumption proof for the {@code com.ishtaran:ishtaran-java} artifact published to the
 * local .m2 — "package dry run" required by the SDK Program brief before considering the Java
 * SDK ready for packaging. Makes no real network call (no real Sandbox/Production is available
 * yet, see SDK_CAPABILITY_SPEC.md §2) — it only proves the classpath resolves and the public API
 * compiles and runs.
 */
public final class MavenConsumerSmokeTest {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .environment(Environment.LOCAL)
                .apiKey("test-key")
                .build();

        if (client.withdrawals() == null || client.accounts() == null || client.auth() == null) {
            throw new IllegalStateException("Client badly constructed");
        }

        boolean verified = WebhookSignatureVerifier.verify(
                "{}", "deadbeef", String.valueOf(System.currentTimeMillis() / 1000), "secret");
        if (verified) {
            throw new IllegalStateException("Invalid signature should not verify");
        }

        System.out.println("MAVEN_CONSUMER_SMOKE_TEST: OK — ishtaran-java successfully consumed via local Maven.");
    }
}
