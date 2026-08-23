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
public final class SettlementStatus {

    public static final SettlementStatus PENDING = new SettlementStatus("PENDING", 0);
    public static final SettlementStatus EXECUTING = new SettlementStatus("EXECUTING", 1);
    public static final SettlementStatus COMPLETED = new SettlementStatus("COMPLETED", 2);
    public static final SettlementStatus FAILED = new SettlementStatus("FAILED", 3);

    private static final Map<Integer, SettlementStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new SettlementStatus[] {PENDING, EXECUTING, COMPLETED, FAILED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private SettlementStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static SettlementStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new SettlementStatus("UNKNOWN", raw));
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
        return o instanceof SettlementStatus other && Objects.equals(other.rawValue, this.rawValue);
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
