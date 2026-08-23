package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Group B — raw integer in the JSON (ExecutionCustody.Contracts.Enums.DerivationScheme, no
 * JsonStringEnumConverter on the backend, SPEC-021/checkpoint 7). Same pattern as {@link DepositStatus}
 * — a class, not a Java {@code enum}, to preserve the raw int of an unknown value (§11.4).
 * Wire-format only — distinct from {@code com.ishtaran.sdk.wallet.DerivationScheme} (a local
 * derivation/signing strategy identifier, never serialized), same module-mirrored pattern
 * as the backend (ExecutionCustody.Domain vs .Contracts).
 */
public final class DerivationScheme {

    public static final DerivationScheme TRON_BIP44_HARDENED_ACCOUNT = new DerivationScheme("TRON_BIP44_HARDENED_ACCOUNT", 1);

    private static final Map<Integer, DerivationScheme> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new DerivationScheme[] {TRON_BIP44_HARDENED_ACCOUNT}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private DerivationScheme(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static DerivationScheme fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new DerivationScheme("UNKNOWN", raw));
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
        return o instanceof DerivationScheme other && Objects.equals(other.rawValue, this.rawValue);
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
