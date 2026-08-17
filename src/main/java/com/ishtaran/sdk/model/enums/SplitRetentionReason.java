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
public final class SplitRetentionReason {

    public static final SplitRetentionReason ACCOUNT_NOT_FOUND = new SplitRetentionReason("ACCOUNT_NOT_FOUND", 0);
    public static final SplitRetentionReason ACCOUNT_NOT_ACTIVE = new SplitRetentionReason("ACCOUNT_NOT_ACTIVE", 1);
    public static final SplitRetentionReason ACCOUNT_NOT_AUTHORIZED_FOR_APPLICATION = new SplitRetentionReason("ACCOUNT_NOT_AUTHORIZED_FOR_APPLICATION", 2);

    private static final Map<Integer, SplitRetentionReason> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new SplitRetentionReason[] {ACCOUNT_NOT_FOUND, ACCOUNT_NOT_ACTIVE, ACCOUNT_NOT_AUTHORIZED_FOR_APPLICATION}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private SplitRetentionReason(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static SplitRetentionReason fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new SplitRetentionReason("UNKNOWN", raw));
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
        return o instanceof SplitRetentionReason other && Objects.equals(other.rawValue, this.rawValue);
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
