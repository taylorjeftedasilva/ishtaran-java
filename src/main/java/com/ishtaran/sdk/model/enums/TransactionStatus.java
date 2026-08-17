package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grupo B — inteiro bruto no JSON (sem JsonStringEnumConverter no backend) (ver SDK_CAPABILITY_SPEC.md §11.3). Classe (não {@code enum} Java) para permitir
 * que um valor desconhecido preserve o int bruto exato recebido em vez de colapsar em uma
 * constante fixa sem informação (§11.4 — forward-compatibility real).
 */
public final class TransactionStatus {

    public static final TransactionStatus CREATED = new TransactionStatus("CREATED", 0);
    public static final TransactionStatus AWAITING_FUNDS = new TransactionStatus("AWAITING_FUNDS", 1);
    public static final TransactionStatus FUNDED = new TransactionStatus("FUNDED", 2);
    public static final TransactionStatus RESERVED = new TransactionStatus("RESERVED", 3);
    public static final TransactionStatus SETTLED = new TransactionStatus("SETTLED", 4);
    public static final TransactionStatus PARTIALLY_REFUNDED = new TransactionStatus("PARTIALLY_REFUNDED", 5);
    public static final TransactionStatus REFUNDED = new TransactionStatus("REFUNDED", 6);
    public static final TransactionStatus FROZEN = new TransactionStatus("FROZEN", 7);
    public static final TransactionStatus CANCELLED = new TransactionStatus("CANCELLED", 8);
    public static final TransactionStatus PARTIALLY_SETTLED = new TransactionStatus("PARTIALLY_SETTLED", 9);

    private static final Map<Integer, TransactionStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new TransactionStatus[] {CREATED, AWAITING_FUNDS, FUNDED, RESERVED, SETTLED, PARTIALLY_REFUNDED, REFUNDED, FROZEN, CANCELLED, PARTIALLY_SETTLED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private TransactionStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static TransactionStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new TransactionStatus("UNKNOWN", raw));
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
        return o instanceof TransactionStatus other && Objects.equals(other.rawValue, this.rawValue);
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
