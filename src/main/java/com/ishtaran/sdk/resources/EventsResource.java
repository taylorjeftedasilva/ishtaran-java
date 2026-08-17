package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.EventIngestionResult;
import com.ishtaran.sdk.model.enums.EventSource;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code Events} (1 rota real: ingestão, mesmo módulo {@code WorkflowRules}). */
public final class EventsResource extends ApiResourceSupport {

    public EventsResource(HttpTransport transport) {
        super(transport);
    }

    public EventIngestionResult ingest(UUID applicationId, UUID workflowVersionId, UUID currentStateId,
                                        UUID transactionReference, UUID eventTypeId, EventSource eventSource,
                                        Map<String, String> payload, String idempotencyKey) {
        var body = new LinkedHashMap<String, Object>();
        body.put("workflowVersionId", workflowVersionId);
        body.put("currentStateId", currentStateId);
        body.put("transactionReference", transactionReference);
        body.put("eventTypeId", eventTypeId);
        body.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        body.put("payload", payload);
        body.put("eventSource", eventSource.rawValue());
        return execute(HttpRequest.post("/v1/applications/" + applicationId + "/events", toJson(body), true),
                EventIngestionResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
