package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code ExecutionCustody.Contracts.Enums.NetworkResourceSource} (SPEC-TRON-RESOURCE-001) — where
 * the physical network resource (e.g. TRON Energy/Bandwidth) came from. Group B — raw integer.
 */
public final class NetworkResourceSource {

    public static final NetworkResourceSource NOT_EVALUATED = new NetworkResourceSource("NOT_EVALUATED", 0);
    public static final NetworkResourceSource SELF = new NetworkResourceSource("SELF", 1);
    public static final NetworkResourceSource ISHTARAN_SPONSORED = new NetworkResourceSource("ISHTARAN_SPONSORED", 2);
    public static final NetworkResourceSource PEER = new NetworkResourceSource("PEER", 3);
    public static final NetworkResourceSource EXTERNAL = new NetworkResourceSource("EXTERNAL", 4);

    private static final Map<Integer, NetworkResourceSource> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new NetworkResourceSource[] {NOT_EVALUATED, SELF, ISHTARAN_SPONSORED, PEER, EXTERNAL}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private NetworkResourceSource(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static NetworkResourceSource fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new NetworkResourceSource("UNKNOWN", raw));
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
        return o instanceof NetworkResourceSource other && Objects.equals(other.rawValue, this.rawValue);
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
