package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code ExecutionCustody.Contracts.Enums.NetworkCostPayer} (SPEC-NETEXEC-001) — who is charged
 * for the quoted network cost. Group B — raw integer in the JSON.
 */
public final class NetworkCostPayer {

    public static final NetworkCostPayer INTEGRATOR = new NetworkCostPayer("INTEGRATOR", 0);
    public static final NetworkCostPayer REQUESTER = new NetworkCostPayer("REQUESTER", 1);

    private static final Map<Integer, NetworkCostPayer> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new NetworkCostPayer[] {INTEGRATOR, REQUESTER}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private NetworkCostPayer(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static NetworkCostPayer fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new NetworkCostPayer("UNKNOWN", raw));
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
        return o instanceof NetworkCostPayer other && Objects.equals(other.rawValue, this.rawValue);
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
