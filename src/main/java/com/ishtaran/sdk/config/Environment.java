package com.ishtaran.sdk.config;

/**
 * Ambientes oficiais do projeto (CLAUDE.md): Local, Sandbox, Production. Puramente informativo a
 * menos que {@link IshtaranClientConfig#baseUrl()} esteja ausente — ver {@link Endpoints}.
 */
public enum Environment {
    LOCAL,
    SANDBOX,
    PRODUCTION,
}
