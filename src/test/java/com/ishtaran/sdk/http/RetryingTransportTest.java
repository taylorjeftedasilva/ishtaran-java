package com.ishtaran.sdk.http;

import com.ishtaran.sdk.config.RetryPolicy;
import com.ishtaran.sdk.error.NetworkError;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** See SDK_CAPABILITY_SPEC.md §8 — retry only on connection/429/idempotent-5xx; never on deterministic 4xx. */
class RetryingTransportTest {

    private static final RetryPolicy FAST_POLICY =
            new RetryPolicy(2, Duration.ofMillis(1), 1.0, Duration.ofMillis(5));

    @Test
    void status400_neverRetried_evenWhenRetriesAvailable() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(400, "{}"));
        var retrying = new RetryingTransport(fake, FAST_POLICY);

        retrying.send(HttpRequest.post("/x", "{}", true));

        assertEquals(1, fake.requestCount());
    }

    @Test
    void status429_retriedUpToMaxRetries_thenReturnsLastResponse() {
        var fake = new FakeHttpTransport()
                .enqueue(FakeHttpTransport.json(429, "{}"))
                .enqueue(FakeHttpTransport.json(429, "{}"))
                .enqueue(FakeHttpTransport.json(429, "{}"));
        var retrying = new RetryingTransport(fake, FAST_POLICY);

        var response = retrying.send(HttpRequest.post("/x", "{}", true));

        assertEquals(429, response.status());
        assertEquals(3, fake.requestCount()); // 1 original + 2 retries (maxRetries=2)
    }

    @Test
    void status503_idempotentRequest_retried() {
        var fake = new FakeHttpTransport()
                .enqueue(FakeHttpTransport.json(503, "{}"))
                .enqueue(FakeHttpTransport.json(200, "{\"ok\":true}"));
        var retrying = new RetryingTransport(fake, FAST_POLICY);

        var response = retrying.send(HttpRequest.get("/x"));

        assertEquals(200, response.status());
        assertEquals(2, fake.requestCount());
    }

    @Test
    void status503_nonIdempotentRequest_neverRetried() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(503, "{}"));
        var retrying = new RetryingTransport(fake, FAST_POLICY);

        var response = retrying.send(HttpRequest.post("/x", "{}", false));

        assertEquals(503, response.status());
        assertEquals(1, fake.requestCount());
    }

    @Test
    void connectionFailure_retriedUpToMaxRetries_thenThrows() {
        var fake = new FakeHttpTransport()
                .enqueueThrow(new NetworkError("conn reset", null))
                .enqueueThrow(new NetworkError("conn reset", null))
                .enqueueThrow(new NetworkError("conn reset", null));
        var retrying = new RetryingTransport(fake, FAST_POLICY);

        assertThrows(NetworkError.class, () -> retrying.send(HttpRequest.get("/x")));
        assertEquals(3, fake.requestCount());
    }

    @Test
    void retryDisabled_neverRetries() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(429, "{}"));
        var retrying = new RetryingTransport(fake, RetryPolicy.disabled());

        retrying.send(HttpRequest.get("/x"));

        assertEquals(1, fake.requestCount());
    }
}
