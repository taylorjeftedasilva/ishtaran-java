package com.ishtaran.sdk.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Ver SDK_CAPABILITY_SPEC.md §2/§13. */
class IshtaranClientConfigTest {

    @Test
    void environmentLocal_withoutExplicitBaseUrl_resolvesToRealLocalDefault() {
        var config = IshtaranClientConfig.builder().environment(Environment.LOCAL).build();
        assertEquals(Endpoints.LOCAL, config.baseUrl());
    }

    @Test
    void environmentSandbox_withoutExplicitBaseUrl_resolvesToRealSandboxDefault() {
        var config = IshtaranClientConfig.builder().environment(Environment.SANDBOX).build();
        assertEquals(Endpoints.SANDBOX, config.baseUrl());
    }

    @Test
    void environmentProduction_withoutExplicitBaseUrl_throwsInsteadOfGuessingDns() {
        var builder = IshtaranClientConfig.builder().environment(Environment.PRODUCTION);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void explicitBaseUrl_alwaysWins_regardlessOfEnvironment() {
        var config = IshtaranClientConfig.builder()
                .environment(Environment.SANDBOX)
                .baseUrl("https://custom.example.com")
                .build();
        assertEquals("https://custom.example.com", config.baseUrl());
    }

    @Test
    void insecureTlsOverride_onlyAllowedForLocal() {
        var builder = IshtaranClientConfig.builder()
                .environment(Environment.SANDBOX)
                .baseUrl("https://custom.example.com")
                .allowInsecureTlsForLocalDevelopment(true);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void insecureTlsOverride_allowedForLocal() {
        var config = IshtaranClientConfig.builder()
                .environment(Environment.LOCAL)
                .allowInsecureTlsForLocalDevelopment(true)
                .build();
        assertTrue(config.allowInsecureTlsForLocalDevelopment());
    }

    @Test
    void toString_neverLeaksApiKeyInPlainText() {
        var config = IshtaranClientConfig.builder()
                .environment(Environment.LOCAL)
                .apiKey("supersecretapikeyvalue1234567890")
                .build();

        assertTrue(config.toString().contains("****"));
        assertEquals(false, config.toString().contains("supersecretapikeyvalue1234567890"));
    }

    @Test
    void defaults_areSaneAndFinite_neverInfiniteTimeout() {
        var config = IshtaranClientConfig.builder().build();
        assertTrue(config.connectTimeout().toMillis() > 0);
        assertTrue(config.requestTimeout().toMillis() > 0);
        assertTrue(config.userAgent().startsWith("ishtaran-java/"));
    }
}
