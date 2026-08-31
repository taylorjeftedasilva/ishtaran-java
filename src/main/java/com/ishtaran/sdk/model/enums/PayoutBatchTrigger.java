package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code Payout.Contracts.Enums.PayoutBatchTrigger} (SPEC-025) — this SDK slice only ever sends
 * MANUAL ({@code CreatePayoutBatchCommand} accepts no other trigger via the public route yet).
 * Group B — raw integer in the JSON.
 */
public final class PayoutBatchTrigger {

    public static final PayoutBatchTrigger THRESHOLD_CROSSED = new PayoutBatchTrigger("THRESHOLD_CROSSED", 0);
    public static final PayoutBatchTrigger SCHEDULED = new PayoutBatchTrigger("SCHEDULED", 1);
    public static final PayoutBatchTrigger MANUAL = new PayoutBatchTrigger("MANUAL", 2);

    private static final Map<Integer, PayoutBatchTrigger> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new PayoutBatchTrigger[] {THRESHOLD_CROSSED, SCHEDULED, MANUAL}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private PayoutBatchTrigger(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static PayoutBatchTrigger fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new PayoutBatchTrigger("UNKNOWN", raw));
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
        return o instanceof PayoutBatchTrigger other && Objects.equals(other.rawValue, this.rawValue);
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
