package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.CreateRuleResult;
import com.ishtaran.sdk.model.dataplane.CreateWorkflowResult;
import com.ishtaran.sdk.model.dataplane.CreateWorkflowVersionResult;
import com.ishtaran.sdk.model.dataplane.StateInput;
import com.ishtaran.sdk.model.dataplane.TransitionInput;
import com.ishtaran.sdk.model.dataplane.ConditionInput;
import com.ishtaran.sdk.model.dataplane.WorkflowResponse;
import com.ishtaran.sdk.model.dataplane.WorkflowVersionResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code WorkflowRules} (7 rotas de Workflow/Version/Rule; Events/EventTypes em recursos próprios). */
public final class WorkflowsResource extends ApiResourceSupport {

    public WorkflowsResource(HttpTransport transport) {
        super(transport);
    }

    public CreateWorkflowResult create(UUID organizationId, String name) {
        var body = toJson(Map.of("name", name));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/workflows", body, false),
                CreateWorkflowResult.class);
    }

    public List<WorkflowResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/workflows"),
                new TypeReference<List<WorkflowResponse>>() {
                });
    }

    public WorkflowResponse get(UUID workflowId) {
        return execute(HttpRequest.get("/v1/workflows/" + workflowId), WorkflowResponse.class);
    }

    public CreateWorkflowVersionResult createVersion(UUID workflowId, List<StateInput> states, List<TransitionInput> transitions) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("states", states);
        payload.put("transitions", transitions);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/workflows/" + workflowId + "/versions", body, false),
                CreateWorkflowVersionResult.class);
    }

    public WorkflowVersionResponse getVersion(UUID workflowId, UUID workflowVersionId) {
        return execute(HttpRequest.get("/v1/workflows/" + workflowId + "/versions/" + workflowVersionId),
                WorkflowVersionResponse.class);
    }

    public void publishVersion(UUID workflowId, UUID workflowVersionId) {
        executeNoContent(HttpRequest.post(
                "/v1/workflows/" + workflowId + "/versions/" + workflowVersionId + "/publish", null, false));
    }

    public CreateRuleResult createRule(UUID workflowId, UUID workflowVersionId, UUID fromStateId, UUID toStateId,
                                        UUID eventTypeId, List<ConditionInput> conditions) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("fromStateId", fromStateId);
        payload.put("toStateId", toStateId);
        payload.put("eventTypeId", eventTypeId);
        payload.put("conditions", conditions);
        var body = toJson(payload);
        return execute(HttpRequest.post(
                "/v1/workflows/" + workflowId + "/versions/" + workflowVersionId + "/rules", body, false),
                CreateRuleResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
