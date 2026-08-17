package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Cobre a idempotência via HEADER (não campo de corpo) — diferente dos módulos financeiros, ver SDK_CAPABILITY_SPEC.md §9. */
class OrganizationsResourceTest {

    @Test
    void create_withoutExplicitIdempotencyKey_autoGeneratesHeader_neverBodyField() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201,
                "{\"id\":\"11111111-1111-1111-1111-111111111111\"}"));
        var resource = new OrganizationsResource(fake);

        resource.create("Acme Inc", null);

        var sentRequest = fake.received().get(0);
        var headerValue = sentRequest.headers().get("Idempotency-Key");
        assertNotNull(headerValue);
        assertTrue(sentRequest.body() == null || !sentRequest.body().contains("idempotencyKey"),
                "idempotencyKey nunca deve ir no corpo para este endpoint (é header real)");
    }

    @Test
    void create_explicitIdempotencyKey_neverOverwritten() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201,
                "{\"id\":\"11111111-1111-1111-1111-111111111111\"}"));
        var resource = new OrganizationsResource(fake);

        resource.create("Acme Inc", "my-explicit-key");

        assertEquals("my-explicit-key", fake.received().get(0).headers().get("Idempotency-Key"));
    }
}
