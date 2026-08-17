package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.ConditionOperator;

public record ConditionResponse(String field, ConditionOperator operator, String expectedValue) {
}
