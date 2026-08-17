package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inteiro bruto no JSON — {@code IdentityAccess.Contracts.Enums.MemberRole} é
 * {@code enum: [1,2,3,4], type: integer} (usado nos REQUESTS {@code InviteMemberRequest.role}/
 * {@code AssignRoleRequest.newRole}). O campo de RESPOSTA {@code MemberResponse.role} é uma string
 * separada (Grupo A, mapeada via {@code .ToString()} — modelado como {@code String} simples no
 * record de resposta, sem precisar desta classe).
 */
public final class MemberRole {

    public static final MemberRole OWNER = new MemberRole("OWNER", 1);
    public static final MemberRole ADMIN = new MemberRole("ADMIN", 2);
    public static final MemberRole FINANCEIRO = new MemberRole("FINANCEIRO", 3);
    public static final MemberRole LEITURA = new MemberRole("LEITURA", 4);

    private static final Map<Integer, MemberRole> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new MemberRole[] {OWNER, ADMIN, FINANCEIRO, LEITURA}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private MemberRole(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static MemberRole fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new MemberRole("UNKNOWN", raw));
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
        return o instanceof MemberRole other && other.rawValue == this.rawValue;
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
