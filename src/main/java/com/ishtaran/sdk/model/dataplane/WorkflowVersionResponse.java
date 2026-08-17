package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkflowVersionResponse(
        UUID workflowVersionId,
        UUID workflowId,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt,
        List<StateResponse> states,
        List<TransitionResponse> transitions,
        List<RuleResponse> rules) {
}
