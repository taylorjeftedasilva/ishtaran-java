package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code Withdrawals.Contracts.Enums.NetworkExecutionCostStatus} (SPEC-026 Descoberta 8) — derived
 * from {@code Withdrawal.Status}, never its own persisted state. Group B — raw integer in the JSON.
 */
public final class NetworkExecutionCostStatus {

    public static final NetworkExecutionCostStatus RESERVED = new NetworkExecutionCostStatus("RESERVED", 0);
    public static final NetworkExecutionCostStatus SETTLED = new NetworkExecutionCostStatus("SETTLED", 1);
    public static final NetworkExecutionCostStatus RELEASED = new NetworkExecutionCostStatus("RELEASED", 2);
    public static final NetworkExecutionCostStatus REQUIRES_RECONCILIATION = new NetworkExecutionCostStatus("REQUIRES_RECONCILIATION", 3);

    private static final Map<Integer, NetworkExecutionCostStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new NetworkExecutionCostStatus[] {RESERVED, SETTLED, RELEASED, REQUIRES_RECONCILIATION}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private NetworkExecutionCostStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static NetworkExecutionCostStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new NetworkExecutionCostStatus("UNKNOWN", raw));
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
        return o instanceof NetworkExecutionCostStatus other && Objects.equals(other.rawValue, this.rawValue);
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
