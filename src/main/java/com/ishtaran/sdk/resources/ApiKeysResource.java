package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.RotateApiKeyResult;
import com.ishtaran.sdk.serialization.JsonCodec;
import com.ishtaran.sdk.util.DotNetTimeSpan;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/** Control Plane — {@code ApiKeys} (2 real routes). */
public final class ApiKeysResource extends ApiResourceSupport {

    public ApiKeysResource(HttpTransport transport) {
        super(transport);
    }

    public void revoke(UUID apiKeyId) {
        executeNoContent(HttpRequest.delete("/v1/api-keys/" + apiKeyId));
    }

    /**
     * {@code overlapWindow} is sent in .NET's real {@code TimeSpan} format (not ISO-8601) —
     * see {@link DotNetTimeSpan}. The new key's {@code plainTextKey} only appears in this response.
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
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
