package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.ApiKeyMetadataResponse;
import com.ishtaran.sdk.model.controlplane.GenerateApiKeyResult;

import java.util.List;
import java.util.UUID;

/**
 * Control Plane — {@code Environments} (2 real routes — there is no real get/list route for the
 * Environment itself, see SDK_CAPABILITY_SPEC.md §11.3).
 */
public final class EnvironmentsResource extends ApiResourceSupport {

    public EnvironmentsResource(HttpTransport transport) {
        super(transport);
    }

    public List<ApiKeyMetadataResponse> listApiKeys(UUID environmentId) {
        return execute(HttpRequest.get("/v1/environments/" + environmentId + "/api-keys"),
                new TypeReference<List<ApiKeyMetadataResponse>>() {
                });
    }

    /** {@code plainTextKey} only appears in this response — never recoverable afterward. */
    public GenerateApiKeyResult generateApiKey(UUID environmentId) {
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/api-keys", "{}", false),
                GenerateApiKeyResult.class);
    }
}
