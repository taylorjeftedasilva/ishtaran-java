package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code Payout.Contracts.Enums.PayoutBatchStatus} (SPEC-025). Group B — raw integer in the JSON.
 */
public final class PayoutBatchStatus {

    public static final PayoutBatchStatus CREATED = new PayoutBatchStatus("CREATED", 0);
    public static final PayoutBatchStatus RESERVED = new PayoutBatchStatus("RESERVED", 1);
    public static final PayoutBatchStatus EXECUTING = new PayoutBatchStatus("EXECUTING", 2);
    public static final PayoutBatchStatus COMPLETED = new PayoutBatchStatus("COMPLETED", 3);
    public static final PayoutBatchStatus PARTIALLY_FAILED = new PayoutBatchStatus("PARTIALLY_FAILED", 4);
    public static final PayoutBatchStatus FAILED = new PayoutBatchStatus("FAILED", 5);

    private static final Map<Integer, PayoutBatchStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new PayoutBatchStatus[] {CREATED, RESERVED, EXECUTING, COMPLETED, PARTIALLY_FAILED, FAILED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private PayoutBatchStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static PayoutBatchStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new PayoutBatchStatus("UNKNOWN", raw));
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
        return o instanceof PayoutBatchStatus other && Objects.equals(other.rawValue, this.rawValue);
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
