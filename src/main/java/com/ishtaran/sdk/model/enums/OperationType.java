package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PROMPT 7 (SPEC-TRANSFER-001) — mirrors Settlement.Contracts.Enums.OperationType. Group B — raw
 * integer in the JSON (no JsonStringEnumConverter on the backend), same pattern as
 * {@link SettlementStatus}. Selects which PricingPolicy rate {@code executeSettlement} applies —
 * MARKETPLACE (0.90%) is the default, unchanged from before this enum existed.
 */
public final class OperationType {

    public static final OperationType MARKETPLACE = new OperationType("MARKETPLACE", 0);
    public static final OperationType PAYMENT = new OperationType("PAYMENT", 1);
    public static final OperationType TRANSFER = new OperationType("TRANSFER", 2);

    private static final Map<Integer, OperationType> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new OperationType[] {MARKETPLACE, PAYMENT, TRANSFER}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private OperationType(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static OperationType fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new OperationType("UNKNOWN", raw));
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
        return o instanceof OperationType other && Objects.equals(other.rawValue, this.rawValue);
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
