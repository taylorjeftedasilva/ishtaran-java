package com.ishtaran.sdk.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedactorTest {

    @Test
    void mask_longSecret_showsOnlyFirstAndLast4Chars() {
        // Real API Key (ApiKeyGenerator.Generate()) is Base64 of 32 bytes, with no sk_live_-style prefix
        // (see SDK_CAPABILITY_SPEC.md §12.5) — masking never assumes a nonexistent prefix.
        String masked = Redactor.mask("QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVoxMjM0NTY3ODkw");
        assertEquals("QUJD****ODkw", masked);
    }

    @Test
    void mask_shortSecret_neverPartiallyLeaked() {
        assertEquals("****", Redactor.mask("short"));
    }

    @Test
    void mask_null_returnsPlaceholder() {
        assertEquals("null", Redactor.mask(null));
    }

    @Test
    void isSensitiveHeader_authorizationAndApiKey_caseInsensitive() {
        assertTrue(Redactor.isSensitiveHeader("Authorization"));
        assertTrue(Redactor.isSensitiveHeader("x-api-key"));
        assertTrue(Redactor.isSensitiveHeader("X-API-KEY"));
    }
}
