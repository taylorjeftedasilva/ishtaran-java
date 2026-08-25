package com.ishtaran.sdk.config;

/**
 * Centralized base URLs — never URL strings scattered across the SDK (rule from the brief). {@link #LOCAL}
 * and {@link #SANDBOX} are real known defaults today. {@code SANDBOX} points at the canonical
 * {@code sandbox-api.ishtaran.com} domain (Cloud Run Domain Mapping, live since 2026-08-25 — the
 * raw Cloud Run URL from the 2026-08-24 deploy still works but is no longer advertised).
 * {@code PRODUCTION} does not have real infrastructure provisioned yet (terraform apply has never
 * run against it) — resolving it without an explicit {@code baseUrl} is a configuration error,
 * never a silent fallback to a made-up domain.
 */
public final class Endpoints {

    public static final String LOCAL = "http://localhost:8080";
    public static final String SANDBOX = "https://sandbox-api.ishtaran.com";

    private Endpoints() {
    }

    /**
     * Resolves the effective base URL: {@code explicitBaseUrl} always wins when present (hard rule
     * from the brief — no business method may override this resolution on its own). When
     * absent, {@link Environment#LOCAL} and {@link Environment#SANDBOX} have real defaults;
     * {@link Environment#PRODUCTION} throws.
     */
    public static String resolve(Environment environment, String explicitBaseUrl) {
        if (explicitBaseUrl != null && !explicitBaseUrl.isBlank()) {
            return explicitBaseUrl;
        }
        if (environment == Environment.LOCAL) {
            return LOCAL;
        }
        if (environment == Environment.SANDBOX) {
            return SANDBOX;
        }
        throw new IllegalStateException(
                "an explicit baseUrl is required for Environment." + environment
                        + " — no real Production URL has been provisioned yet "
                        + "(see SDK_CAPABILITY_SPEC.md §2). Configure IshtaranClientConfig.baseUrl(...) "
                        + "explicitly.");
    }
}
