package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Inteiro bruto no JSON — {@code WorkflowRules.Contracts.Enums.EventSource} (enum: [1,2,3]). */
public final class EventSource {

    public static final EventSource APPLICATION = new EventSource("APPLICATION", 1);
    public static final EventSource PLATFORM_TIMER = new EventSource("PLATFORM_TIMER", 2);
    public static final EventSource MANUAL_REVIEW = new EventSource("MANUAL_REVIEW", 3);

    private static final Map<Integer, EventSource> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new EventSource[] {APPLICATION, PLATFORM_TIMER, MANUAL_REVIEW}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private EventSource(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static EventSource fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new EventSource("UNKNOWN", raw));
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
        return o instanceof EventSource other && other.rawValue == this.rawValue;
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
