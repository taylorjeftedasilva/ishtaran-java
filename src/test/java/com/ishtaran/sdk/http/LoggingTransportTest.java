package com.ishtaran.sdk.http;

import com.ishtaran.sdk.error.NetworkError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** See SDK_CAPABILITY_SPEC.md §14 — opt-in, never logs Authorization/X-Api-Key in plain text. */
class LoggingTransportTest {

    @Test
    void delegatesAndReturnsRealResponse() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{\"ok\":true}"));
        var logging = new LoggingTransport(fake);

        var response = logging.send(HttpRequest.get("/x"));

        assertEquals(200, response.status());
        assertEquals(1, fake.requestCount());
    }

    @Test
    void exceptionFromDelegate_stillPropagates() {
        var fake = new FakeHttpTransport().enqueueThrow(new NetworkError("boom", null));
        var logging = new LoggingTransport(fake);

        assertThrows(NetworkError.class, () -> logging.send(HttpRequest.get("/x")));
    }

    @Test
    void redactedHeaders_neverExposesApiKeyOrAuthorizationInPlainText() {
        var logging = new LoggingTransport(new FakeHttpTransport());
        var request = HttpRequest.get("/x")
                .header("X-Api-Key", "supersecretapikeyvalue1234567890")
                .header("Authorization", "Bearer supersecretjwttoken1234567890")
                .header("User-Agent", "ishtaran-java/1.0.0");

        var rendered = logging.redactedHeaders(request);

        assertFalse(rendered.contains("supersecretapikeyvalue1234567890"));
        assertFalse(rendered.contains("supersecretjwttoken1234567890"));
        assertTrue(rendered.contains("****"));
        assertTrue(rendered.contains("ishtaran-java/1.0.0"), "non-sensitive headers remain visible");
    }
}
