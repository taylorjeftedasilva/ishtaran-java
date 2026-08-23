package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Group A — human-readable string in the JSON (the module calls .ToString() in the Mapping layer) (see SDK_CAPABILITY_SPEC.md §11.3). A class (not a Java {@code enum}) so
 * an unknown value can preserve the exact raw String received instead of collapsing into an
 * uninformative fixed constant (§11.4 — real forward-compatibility).
 */
public final class CatalogEntryStatus {

    public static final CatalogEntryStatus ENABLED = new CatalogEntryStatus("ENABLED", "Enabled");
    public static final CatalogEntryStatus DISABLED = new CatalogEntryStatus("DISABLED", "Disabled");

    private static final Map<String, CatalogEntryStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new CatalogEntryStatus[] {ENABLED, DISABLED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final String rawValue;

    private CatalogEntryStatus(String name, String rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static CatalogEntryStatus fromRaw(String raw) {
        return KNOWN.getOrDefault(raw, new CatalogEntryStatus("UNKNOWN", raw));
    }

    @JsonValue
    public String rawValue() {
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
        return o instanceof CatalogEntryStatus other && Objects.equals(other.rawValue, this.rawValue);
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
