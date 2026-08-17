package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grupo A — string legível no JSON (o módulo chama .ToString() na camada de Mapping) (ver SDK_CAPABILITY_SPEC.md §11.3). Classe (não {@code enum} Java) para permitir
 * que um valor desconhecido preserve o String bruto exato recebido em vez de colapsar em uma
 * constante fixa sem informação (§11.4 — forward-compatibility real).
 */
public final class WorkflowStatus {

    public static final WorkflowStatus DRAFT = new WorkflowStatus("DRAFT", "Draft");
    public static final WorkflowStatus PUBLISHED = new WorkflowStatus("PUBLISHED", "Published");
    public static final WorkflowStatus DEPRECATED = new WorkflowStatus("DEPRECATED", "Deprecated");

    private static final Map<String, WorkflowStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new WorkflowStatus[] {DRAFT, PUBLISHED, DEPRECATED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final String rawValue;

    private WorkflowStatus(String name, String rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static WorkflowStatus fromRaw(String raw) {
        return KNOWN.getOrDefault(raw, new WorkflowStatus("UNKNOWN", raw));
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
        return o instanceof WorkflowStatus other && Objects.equals(other.rawValue, this.rawValue);
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
