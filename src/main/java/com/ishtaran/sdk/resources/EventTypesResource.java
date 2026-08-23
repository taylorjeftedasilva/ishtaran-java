package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.CreateEventTypeResult;
import com.ishtaran.sdk.model.dataplane.EventTypeResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code EventTypes} (2 real routes, same {@code WorkflowRules} module). */
public final class EventTypesResource extends ApiResourceSupport {

    public EventTypesResource(HttpTransport transport) {
        super(transport);
    }

    public CreateEventTypeResult create(UUID organizationId, String name) {
        var body = toJson(Map.of("name", name));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/event-types", body, false),
                CreateEventTypeResult.class);
    }

    public List<EventTypeResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/event-types"),
                new TypeReference<List<EventTypeResponse>>() {
                });
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
