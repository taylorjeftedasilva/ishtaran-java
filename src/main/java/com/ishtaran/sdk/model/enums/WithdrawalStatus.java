package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grupo B (inteiro bruto no JSON — sem JsonStringEnumConverter no backend, ver
 * SDK_CAPABILITY_SPEC.md §11.3). Classe (não {@code enum} Java) para permitir que um valor
 * desconhecido preserve o inteiro bruto exato recebido, em vez de colapsar em uma constante fixa
 * sem informação (§11.4 — forward-compatibility real, não apenas um rótulo "UNKNOWN" genérico).
 */
public final class WithdrawalStatus {

    public static final WithdrawalStatus REQUESTED = new WithdrawalStatus("REQUESTED", 0);
    public static final WithdrawalStatus VALIDATING = new WithdrawalStatus("VALIDATING", 1);
    public static final WithdrawalStatus PENDING_APPROVAL = new WithdrawalStatus("PENDING_APPROVAL", 2);
    public static final WithdrawalStatus APPROVED = new WithdrawalStatus("APPROVED", 3);
    public static final WithdrawalStatus REJECTED = new WithdrawalStatus("REJECTED", 4);
    public static final WithdrawalStatus BROADCASTING = new WithdrawalStatus("BROADCASTING", 5);
    public static final WithdrawalStatus BROADCAST_FAILED = new WithdrawalStatus("BROADCAST_FAILED", 6);
    public static final WithdrawalStatus CONFIRMING = new WithdrawalStatus("CONFIRMING", 7);
    public static final WithdrawalStatus COMPLETED = new WithdrawalStatus("COMPLETED", 8);
    public static final WithdrawalStatus CANCELLED = new WithdrawalStatus("CANCELLED", 9);

    private static final Map<Integer, WithdrawalStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new WithdrawalStatus[] {REQUESTED, VALIDATING, PENDING_APPROVAL, APPROVED, REJECTED,
                BROADCASTING, BROADCAST_FAILED, CONFIRMING, COMPLETED, CANCELLED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private WithdrawalStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static WithdrawalStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new WithdrawalStatus("UNKNOWN", raw));
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
        return o instanceof WithdrawalStatus other && other.rawValue == this.rawValue;
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
