package com.ishtaran.samples;

import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

public final class GradleConsumerSmokeTest {

    public static void main(String[] args) {
        var client = IshtaranClient.builder()
                .environment(Environment.LOCAL)
                .apiKey("test-key")
                .build();

        if (client.ledger() == null) {
            throw new IllegalStateException("Client badly constructed");
        }

        System.out.println("GRADLE_CONSUMER_SMOKE_TEST: OK — ishtaran-java successfully consumed via Gradle (mavenLocal()).");
    }
}
