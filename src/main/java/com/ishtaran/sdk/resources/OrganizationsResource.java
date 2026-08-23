package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.ApplicationResponse;
import com.ishtaran.sdk.model.controlplane.CreatedResourceResponse;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.controlplane.OrganizationResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Control Plane — {@code Organizations} (6 real routes). Always Member JWT (never API Key — see
 * SDK_CAPABILITY_SPEC.md §4). {@code create}/{@code createApplication} use idempotency via the
 * {@code Idempotency-Key} HEADER — different from the body-field pattern of the financial modules
 * (see SDK_CAPABILITY_SPEC.md §9); never auto-generated here (truly optional on the real backend).
 */
public final class OrganizationsResource extends ApiResourceSupport {

    public OrganizationsResource(HttpTransport transport) {
        super(transport);
    }

    /** The only real route without authentication — the system's entry point. */
    public CreatedResourceResponse create(String name, String idempotencyKey) {
        var body = toJson(Map.of("name", name));
        return execute(HttpRequest.post("/v1/organizations", body, false)
                .header("Idempotency-Key", IdempotencyKeyGenerator.resolve(idempotencyKey)), CreatedResourceResponse.class);
    }

    public OrganizationResponse get(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId), OrganizationResponse.class);
    }

    public void suspend(UUID organizationId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/organizations/" + organizationId + "/suspend", body, false));
    }

    public void reactivate(UUID organizationId) {
        executeNoContent(HttpRequest.post("/v1/organizations/" + organizationId + "/reactivate", null, false));
    }

    public List<ApplicationResponse> listApplications(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/applications"),
                new TypeReference<List<ApplicationResponse>>() {
                });
    }

    public CreatedResourceResponse createApplication(UUID organizationId, String name, String idempotencyKey) {
        var body = toJson(Map.of("name", name));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/applications", body, false)
                .header("Idempotency-Key", IdempotencyKeyGenerator.resolve(idempotencyKey)), CreatedResourceResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
