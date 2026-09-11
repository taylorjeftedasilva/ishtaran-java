package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PROMPT 5 section 9 (G.7) -- mirrors Transactions.Contracts.Enums.ExecutionStatus. Group B (raw
 * integer in the JSON), same forward-compatibility class-not-enum pattern as {@link WithdrawalStatus}.
 */
public final class ExecutionStatus {

    public static final ExecutionStatus PREPARED = new ExecutionStatus("PREPARED", 0);
    public static final ExecutionStatus AWAITING_SIGNATURE = new ExecutionStatus("AWAITING_SIGNATURE", 1);
    public static final ExecutionStatus EXECUTED = new ExecutionStatus("EXECUTED", 2);
    public static final ExecutionStatus FAILED = new ExecutionStatus("FAILED", 3);
    public static final ExecutionStatus EXPIRED = new ExecutionStatus("EXPIRED", 4);
    public static final ExecutionStatus OVERDUE = new ExecutionStatus("OVERDUE", 5);

    private static final Map<Integer, ExecutionStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new ExecutionStatus[] {PREPARED, AWAITING_SIGNATURE, EXECUTED, FAILED, EXPIRED, OVERDUE}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private ExecutionStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static ExecutionStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new ExecutionStatus("UNKNOWN", raw));
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
        return o instanceof ExecutionStatus other && other.rawValue == this.rawValue;
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
