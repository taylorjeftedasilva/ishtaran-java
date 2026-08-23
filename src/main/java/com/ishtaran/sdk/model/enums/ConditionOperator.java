package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Raw integer in the JSON — {@code WorkflowRules.Contracts.Enums.ConditionOperator} (enum: [1,2,3]). */
public final class ConditionOperator {

    public static final ConditionOperator EQUALS = new ConditionOperator("EQUALS", 1);
    public static final ConditionOperator GREATER_THAN_OR_EQUAL = new ConditionOperator("GREATER_THAN_OR_EQUAL", 2);
    public static final ConditionOperator LESS_THAN_OR_EQUAL = new ConditionOperator("LESS_THAN_OR_EQUAL", 3);

    private static final Map<Integer, ConditionOperator> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new ConditionOperator[] {EQUALS, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private ConditionOperator(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static ConditionOperator fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new ConditionOperator("UNKNOWN", raw));
    }

    @JsonValue
    public int rawValue() {
        return rawValue;
    }

    public String name() {
        return name;
    }

    public boolean isUnknown() {
        return !KNOWN.containsKey(rawValue);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConditionOperator other && other.rawValue == this.rawValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString() {
        return name + "(" + rawValue + ")";
    }
}
