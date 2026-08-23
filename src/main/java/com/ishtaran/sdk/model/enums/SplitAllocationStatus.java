package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Group B — raw integer in the JSON (no JsonStringEnumConverter on the backend) (see SDK_CAPABILITY_SPEC.md §11.3). A class (not a Java {@code enum}) so
 * an unknown value can preserve the exact raw int received instead of collapsing into an
 * uninformative fixed constant (§11.4 — real forward-compatibility).
 */
public final class SplitAllocationStatus {

    public static final SplitAllocationStatus EXECUTED = new SplitAllocationStatus("EXECUTED", 0);
    public static final SplitAllocationStatus RETAINED = new SplitAllocationStatus("RETAINED", 1);
    public static final SplitAllocationStatus RELEASED = new SplitAllocationStatus("RELEASED", 2);

    private static final Map<Integer, SplitAllocationStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new SplitAllocationStatus[] {EXECUTED, RETAINED, RELEASED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private SplitAllocationStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static SplitAllocationStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new SplitAllocationStatus("UNKNOWN", raw));
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
        return o instanceof SplitAllocationStatus other && Objects.equals(other.rawValue, this.rawValue);
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
