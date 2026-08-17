package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.RotateApiKeyResult;
import com.ishtaran.sdk.serialization.JsonCodec;
import com.ishtaran.sdk.util.DotNetTimeSpan;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/** Control Plane — {@code ApiKeys} (2 rotas reais). */
public final class ApiKeysResource extends ApiResourceSupport {

    public ApiKeysResource(HttpTransport transport) {
        super(transport);
    }

    public void revoke(UUID apiKeyId) {
        executeNoContent(HttpRequest.delete("/v1/api-keys/" + apiKeyId));
    }

    /**
     * {@code overlapWindow} é enviado no formato real de {@code TimeSpan} do .NET (não ISO-8601) —
     * ver {@link DotNetTimeSpan}. {@code plainTextKey} da nova chave só aparece nesta resposta.
     */
    public RotateApiKeyResult rotate(UUID apiKeyId, Duration overlapWindow) {
        var body = toJson(Map.of("overlapWindow", DotNetTimeSpan.format(overlapWindow)));
        return execute(HttpRequest.post("/v1/api-keys/" + apiKeyId + "/rotate", body, false),
                RotateApiKeyResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
