package com.ishtaran.sdk.idempotency;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/** Ver SDK_CAPABILITY_SPEC.md §9. */
class IdempotencyKeyGeneratorTest {

    @Test
    void resolve_explicitKeyProvided_neverOverwritten() {
        String explicit = "my-explicit-key-123";
        assertEquals(explicit, IdempotencyKeyGenerator.resolve(explicit));
    }

    @Test
    void resolve_nullOrBlank_generatesValidUuidV4() {
        String generated = IdempotencyKeyGenerator.resolve(null);
        assertEquals(4, UUID.fromString(generated).version());

        String generatedFromBlank = IdempotencyKeyGenerator.resolve("  ");
        assertEquals(4, UUID.fromString(generatedFromBlank).version());
    }

    @Test
    void generate_twoCalls_neverProduceSameKey() {
        assertNotEquals(IdempotencyKeyGenerator.generate(), IdempotencyKeyGenerator.generate());
    }
}
