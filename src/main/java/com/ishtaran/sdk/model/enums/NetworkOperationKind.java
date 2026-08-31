package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code ExecutionCustody.Contracts.Enums.NetworkOperationKind} (SPEC-NETEXEC-001) — the kind of
 * physical on-chain operation a NetworkExecutionQuote is priced for. Group B — raw integer.
 */
public final class NetworkOperationKind {

    public static final NetworkOperationKind TRANSFER = new NetworkOperationKind("TRANSFER", 0);
    public static final NetworkOperationKind SWAP = new NetworkOperationKind("SWAP", 1);
    public static final NetworkOperationKind STAKE = new NetworkOperationKind("STAKE", 2);
    public static final NetworkOperationKind UNSTAKE = new NetworkOperationKind("UNSTAKE", 3);
    public static final NetworkOperationKind DELEGATE = new NetworkOperationKind("DELEGATE", 4);
    public static final NetworkOperationKind UNDELEGATE = new NetworkOperationKind("UNDELEGATE", 5);

    private static final Map<Integer, NetworkOperationKind> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new NetworkOperationKind[] {TRANSFER, SWAP, STAKE, UNSTAKE, DELEGATE, UNDELEGATE}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private NetworkOperationKind(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static NetworkOperationKind fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new NetworkOperationKind("UNKNOWN", raw));
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
        return o instanceof NetworkOperationKind other && Objects.equals(other.rawValue, this.rawValue);
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
