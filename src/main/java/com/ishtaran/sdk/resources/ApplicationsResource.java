package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.CreatedResourceResponse;
import com.ishtaran.sdk.model.enums.EnvironmentType;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.Map;
import java.util.UUID;

/** Control Plane — {@code Applications} (4 real routes, plus create in {@link OrganizationsResource}). */
public final class ApplicationsResource extends ApiResourceSupport {

    public ApplicationsResource(HttpTransport transport) {
        super(transport);
    }

    public void archive(UUID applicationId) {
        executeNoContent(HttpRequest.post("/v1/applications/" + applicationId + "/archive", null, false));
    }

    public void suspend(UUID applicationId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/applications/" + applicationId + "/suspend", body, false));
    }

    public void reactivate(UUID applicationId) {
        executeNoContent(HttpRequest.post("/v1/applications/" + applicationId + "/reactivate", null, false));
    }

    public CreatedResourceResponse createEnvironment(UUID applicationId, EnvironmentType type) {
        var body = toJson(Map.of("type", type.rawValue()));
        return execute(HttpRequest.post("/v1/applications/" + applicationId + "/environments", body, false),
                CreatedResourceResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
