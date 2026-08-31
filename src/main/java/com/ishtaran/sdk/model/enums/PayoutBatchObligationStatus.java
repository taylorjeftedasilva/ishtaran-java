package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code Payout.Contracts.Enums.PayoutBatchObligationStatus} (SPEC-025). Group B — raw integer.
 */
public final class PayoutBatchObligationStatus {

    public static final PayoutBatchObligationStatus INCLUDED = new PayoutBatchObligationStatus("INCLUDED", 0);
    public static final PayoutBatchObligationStatus CONFIRMED = new PayoutBatchObligationStatus("CONFIRMED", 1);
    public static final PayoutBatchObligationStatus FAILED = new PayoutBatchObligationStatus("FAILED", 2);
    public static final PayoutBatchObligationStatus REQUIRES_RECONCILIATION = new PayoutBatchObligationStatus("REQUIRES_RECONCILIATION", 3);

    private static final Map<Integer, PayoutBatchObligationStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new PayoutBatchObligationStatus[] {INCLUDED, CONFIRMED, FAILED, REQUIRES_RECONCILIATION}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private PayoutBatchObligationStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static PayoutBatchObligationStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new PayoutBatchObligationStatus("UNKNOWN", raw));
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
        return o instanceof PayoutBatchObligationStatus other && Objects.equals(other.rawValue, this.rawValue);
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
