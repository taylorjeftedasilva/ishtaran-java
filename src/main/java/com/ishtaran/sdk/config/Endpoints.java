package com.ishtaran.sdk.config;

/**
 * Centralized base URLs — never URL strings scattered across the SDK (rule from the brief). {@link #LOCAL}
 * is the only known real default today (local docker-compose). {@code SANDBOX}/{@code PRODUCTION}
 * do not have real DNS registered yet (terraform apply has never run against real infrastructure — see
 * SDK_CAPABILITY_SPEC.md §2) — resolving either of those without an explicit {@code baseUrl} is a
 * configuration error, never a silent fallback to a made-up domain.
 */
public final class Endpoints {

    public static final String LOCAL = "http://localhost:8080";

    private Endpoints() {
    }

    /**
     * Resolves the effective base URL: {@code explicitBaseUrl} always wins when present (hard rule
     * from the brief — no business method may override this resolution on its own). When
     * absent, only {@link Environment#LOCAL} has a real default; the others throw.
     */
    public static String resolve(Environment environment, String explicitBaseUrl) {
        if (explicitBaseUrl != null && !explicitBaseUrl.isBlank()) {
            return explicitBaseUrl;
        }
        if (environment == Environment.LOCAL) {
            return LOCAL;
        }
        throw new IllegalStateException(
                "an explicit baseUrl is required for Environment." + environment
                        + " — no real Sandbox/Production URL has been provisioned yet "
                        + "(see SDK_CAPABILITY_SPEC.md §2). Configure IshtaranClientConfig.baseUrl(...) "
                        + "explicitly.");
    }
}
