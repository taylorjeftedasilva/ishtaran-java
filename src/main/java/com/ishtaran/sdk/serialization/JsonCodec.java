package com.ishtaran.sdk.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A single {@link ObjectMapper}, configured once -- never instantiated ad hoc by resources.
 * {@code FAIL_ON_UNKNOWN_PROPERTIES=false}: a new field the server starts sending never breaks
 * deserialization (forward compatibility, same spirit as the enum UNKNOWN fallback -- see
 * SDK_CAPABILITY_SPEC.md section 11.4). Monetary fields use {@code BigDecimal} in the model
 * records -- Jackson reads the raw numeric token straight into BigDecimal, never passing through
 * {@code double} (preserves exact precision even though the API sends {@code number(double)} in
 * the schema, see section 11.1). {@code WRITE_DATES_AS_TIMESTAMPS} disabled -- Jackson's default
 * serializes {@code OffsetDateTime} as a numeric epoch ({@code 1798761600.000000000}), never
 * ISO-8601; the real API ({@code System.Text.Json}/{@code DateTimeOffset}) requires ISO-8601
 * (SDK_CAPABILITY_SPEC.md section 11.2) and rejects the numeric format with a 400 -- a real bug,
 * latent until the first live validation of a route with a date field in the request body (no
 * existing unit test covered this, only a fake transport; discovered during the Self-Custody
 * Signing E2E validation, checkpoint 8).
 */
public final class JsonCodec {

    private static final ObjectMapper MAPPER = build();

    private JsonCodec() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    private static ObjectMapper build() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }
}
