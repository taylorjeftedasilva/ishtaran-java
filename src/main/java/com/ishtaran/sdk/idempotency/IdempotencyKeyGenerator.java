package com.ishtaran.sdk.idempotency;

import java.util.UUID;

/**
 * Generates the {@code idempotencyKey} (a body field, not a header — confirmed in the real schemas, see
 * SDK_CAPABILITY_SPEC.md §9) when the consumer does not provide one explicitly. UUID v4 — the same
 * format accepted by the API's real {@code Guid} fields.
 */
public final class IdempotencyKeyGenerator {

    private IdempotencyKeyGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    /** Never generates a new key if the consumer already provided one — an explicit key always wins. */
    public static String resolve(String explicitKey) {
        return explicitKey != null && !explicitKey.isBlank() ? explicitKey : generate();
    }
}
