package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inteiro bruto no JSON — {@code OrganizationTenancy.Contracts.Enums.EnvironmentType} é
 * {@code enum: [1,2], type: integer} no OpenAPI real (confirmado diretamente, corrigindo uma
 * suposição incorreta anterior de que seria string). Usado hoje só como campo de REQUEST
 * (`CreateEnvironmentRequest.type`) — não existe {@code EnvironmentResponse} real na API (o módulo
 * `Environments` só tem 2 rotas, nenhuma devolve esse schema).
 */
public final class EnvironmentType {

    public static final EnvironmentType SANDBOX = new EnvironmentType("SANDBOX", 1);
    public static final EnvironmentType PRODUCTION = new EnvironmentType("PRODUCTION", 2);

    private static final Map<Integer, EnvironmentType> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new EnvironmentType[] {SANDBOX, PRODUCTION}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private EnvironmentType(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static EnvironmentType fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new EnvironmentType("UNKNOWN", raw));
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
        return o instanceof EnvironmentType other && other.rawValue == this.rawValue;
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
