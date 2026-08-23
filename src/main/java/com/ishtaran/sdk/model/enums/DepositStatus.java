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
public final class DepositStatus {

    public static final DepositStatus DETECTED = new DepositStatus("DETECTED", 0);
    public static final DepositStatus CONFIRMING = new DepositStatus("CONFIRMING", 1);
    public static final DepositStatus CONFIRMED = new DepositStatus("CONFIRMED", 2);
    public static final DepositStatus UNDER_REVIEW = new DepositStatus("UNDER_REVIEW", 3);
    public static final DepositStatus REORG_DETECTED = new DepositStatus("REORG_DETECTED", 4);
    public static final DepositStatus REJECTED = new DepositStatus("REJECTED", 5);

    private static final Map<Integer, DepositStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new DepositStatus[] {DETECTED, CONFIRMING, CONFIRMED, UNDER_REVIEW, REORG_DETECTED, REJECTED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private DepositStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static DepositStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new DepositStatus("UNKNOWN", raw));
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
        return o instanceof DepositStatus other && Objects.equals(other.rawValue, this.rawValue);
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
