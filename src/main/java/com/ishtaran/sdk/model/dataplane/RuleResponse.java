package com.ishtaran.sdk.model.dataplane;

import java.util.List;
import java.util.UUID;

public record RuleResponse(UUID ruleId, UUID fromStateId, UUID toStateId, UUID eventTypeId, List<ConditionResponse> conditions) {
}
