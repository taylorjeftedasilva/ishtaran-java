package com.ishtaran.sdk.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * {@link ObjectMapper} único, configurado uma vez — nunca instanciado ad-hoc pelos resources.
 * {@code FAIL_ON_UNKNOWN_PROPERTIES=false}: um campo novo que o servidor passe a enviar nunca quebra
 * a desserialização (forward-compatibility, mesmo espírito do fallback UNKNOWN de enum — ver
 * SDK_CAPABILITY_SPEC.md §11.4). Campos monetários usam {@code BigDecimal} nos records de model —
 * Jackson lê o token numérico bruto direto para BigDecimal, nunca passando por {@code double}
 * (preserva a precisão exata mesmo a API enviando {@code number(double)} no schema, ver §11.1).
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
        return mapper;
    }
}
