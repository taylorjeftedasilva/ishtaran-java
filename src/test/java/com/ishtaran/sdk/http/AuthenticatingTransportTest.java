package com.ishtaran.sdk.http;

import com.ishtaran.sdk.auth.BearerTokenHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthenticatingTransportTest {

    @Test
    void apiKeyConfigured_attachedAsXApiKeyHeader() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{}"));
        var transport = new AuthenticatingTransport(fake, "my-api-key", new BearerTokenHolder());

        transport.send(HttpRequest.get("/x"));

        assertEquals("my-api-key", fake.received().get(0).headers().get("X-Api-Key"));
    }

    @Test
    void bearerTokenSetAfterConstruction_attachedToSubsequentRequests() {
        var fake = new FakeHttpTransport()
                .enqueue(FakeHttpTransport.json(200, "{}"))
                .enqueue(FakeHttpTransport.json(200, "{}"));
        var holder = new BearerTokenHolder();
        var transport = new AuthenticatingTransport(fake, null, holder);

        transport.send(HttpRequest.get("/before-login"));
        assertNull(fake.received().get(0).headers().get("Authorization"));

        holder.set("real-jwt-token");
        transport.send(HttpRequest.get("/after-login"));
        assertEquals("Bearer real-jwt-token", fake.received().get(1).headers().get("Authorization"));
    }

    @Test
    void noApiKeyNoToken_neitherHeaderAttached() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{}"));
        var transport = new AuthenticatingTransport(fake, null, new BearerTokenHolder());

        transport.send(HttpRequest.get("/x"));

        assertNull(fake.received().get(0).headers().get("X-Api-Key"));
        assertNull(fake.received().get(0).headers().get("Authorization"));
    }
}
