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
public final class RefundStatus {

    public static final RefundStatus REQUESTED = new RefundStatus("REQUESTED", 0);
    public static final RefundStatus APPROVED = new RefundStatus("APPROVED", 1);
    public static final RefundStatus EXECUTED = new RefundStatus("EXECUTED", 2);
    public static final RefundStatus REJECTED = new RefundStatus("REJECTED", 3);

    private static final Map<Integer, RefundStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new RefundStatus[] {REQUESTED, APPROVED, EXECUTED, REJECTED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private RefundStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static RefundStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new RefundStatus("UNKNOWN", raw));
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
        return o instanceof RefundStatus other && Objects.equals(other.rawValue, this.rawValue);
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
