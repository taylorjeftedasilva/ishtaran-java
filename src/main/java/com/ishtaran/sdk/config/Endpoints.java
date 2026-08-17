package com.ishtaran.sdk.config;

/**
 * Base URLs centralizadas — nunca strings de URL espalhadas pelo SDK (regra do brief). {@link #LOCAL}
 * é o único default real conhecido hoje (docker-compose local). {@code SANDBOX}/{@code PRODUCTION}
 * não têm DNS real registrado ainda (terraform apply nunca rodou contra infraestrutura real — ver
 * SDK_CAPABILITY_SPEC.md §2) — resolver essas duas sem um {@code baseUrl} explícito é um erro de
 * configuração, nunca um fallback silencioso para um domínio inventado.
 */
public final class Endpoints {

    public static final String LOCAL = "http://localhost:8080";

    private Endpoints() {
    }

    /**
     * Resolve a base URL efetiva: {@code explicitBaseUrl} sempre vence quando presente (regra dura
     * do brief — nenhum método de negócio pode substituir esta resolução por conta própria). Quando
     * ausente, só {@link Environment#LOCAL} tem um default real; os demais lançam.
     */
    public static String resolve(Environment environment, String explicitBaseUrl) {
        if (explicitBaseUrl != null && !explicitBaseUrl.isBlank()) {
            return explicitBaseUrl;
        }
        if (environment == Environment.LOCAL) {
            return LOCAL;
        }
        throw new IllegalStateException(
                "baseUrl explícito é obrigatório para Environment." + environment
                        + " — nenhuma URL real de Sandbox/Production foi provisionada ainda "
                        + "(ver SDK_CAPABILITY_SPEC.md §2). Configure IshtaranClientConfig.baseUrl(...) "
                        + "explicitamente.");
    }
}
