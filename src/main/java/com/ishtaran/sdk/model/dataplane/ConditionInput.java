package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.ConditionOperator;

public record ConditionInput(String field, ConditionOperator operator, String expectedValue) {
}
