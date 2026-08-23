package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Group A — human-readable string in the JSON (the module calls .ToString() in the Mapping layer) (see SDK_CAPABILITY_SPEC.md §11.3). A class (not a Java {@code enum}) so
 * an unknown value can preserve the exact raw String received instead of collapsing into an
 * uninformative fixed constant (§11.4 — real forward-compatibility).
 */
public final class WorkflowStatus {

    public static final WorkflowStatus DRAFT = new WorkflowStatus("DRAFT", "Draft");
    public static final WorkflowStatus PUBLISHED = new WorkflowStatus("PUBLISHED", "Published");
    public static final WorkflowStatus DEPRECATED = new WorkflowStatus("DEPRECATED", "Deprecated");

    private static final Map<String, WorkflowStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new WorkflowStatus[] {DRAFT, PUBLISHED, DEPRECATED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final String rawValue;

    private WorkflowStatus(String name, String rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static WorkflowStatus fromRaw(String raw) {
        return KNOWN.getOrDefault(raw, new WorkflowStatus("UNKNOWN", raw));
    }

    @JsonValue
    public String rawValue() {
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
        return o instanceof WorkflowStatus other && Objects.equals(other.rawValue, this.rawValue);
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
