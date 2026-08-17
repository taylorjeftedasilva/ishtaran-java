package com.ishtaran.sdk.idempotency;

import java.util.UUID;

/**
 * Gera a {@code idempotencyKey} (campo de corpo, não header — confirmado nos schemas reais, ver
 * SDK_CAPABILITY_SPEC.md §9) quando o consumidor não fornece uma explicitamente. UUID v4 — mesmo
 * formato aceito pelos campos {@code Guid} reais da API.
 */
public final class IdempotencyKeyGenerator {

    private IdempotencyKeyGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    /** Nunca gera uma nova chave se o consumidor já forneceu uma — sobrescrita explícita sempre vence. */
    public static String resolve(String explicitKey) {
        return explicitKey != null && !explicitKey.isBlank() ? explicitKey : generate();
    }
}
