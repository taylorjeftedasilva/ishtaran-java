package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Inteiro bruto no JSON — {@code Sandbox.Contracts.Enums.SimulatedBroadcastOutcome} (enum: [1,2]). */
public final class SimulatedBroadcastOutcome {

    public static final SimulatedBroadcastOutcome ACCEPTED = new SimulatedBroadcastOutcome("ACCEPTED", 1);
    public static final SimulatedBroadcastOutcome FAILED = new SimulatedBroadcastOutcome("FAILED", 2);

    private static final Map<Integer, SimulatedBroadcastOutcome> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new SimulatedBroadcastOutcome[] {ACCEPTED, FAILED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private SimulatedBroadcastOutcome(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static SimulatedBroadcastOutcome fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new SimulatedBroadcastOutcome("UNKNOWN", raw));
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
        return o instanceof SimulatedBroadcastOutcome other && other.rawValue == this.rawValue;
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
