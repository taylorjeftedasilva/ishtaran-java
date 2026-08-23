package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raw integer in JSON — {@code IdentityAccess.Contracts.Enums.MemberRole} is
 * {@code enum: [1,2,3,4], type: integer} (used in REQUESTS {@code InviteMemberRequest.role}/
 * {@code AssignRoleRequest.newRole}). The RESPONSE field {@code MemberResponse.role} is a separate
 * string (Group A, mapped via {@code .ToString()} — modeled as a plain {@code String} in the
 * response record, no need for this class).
 */
public final class MemberRole {

    public static final MemberRole OWNER = new MemberRole("OWNER", 1);
    public static final MemberRole ADMIN = new MemberRole("ADMIN", 2);
    public static final MemberRole FINANCE = new MemberRole("FINANCE", 3);
    public static final MemberRole READ_ONLY = new MemberRole("READ_ONLY", 4);

    private static final Map<Integer, MemberRole> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new MemberRole[] {OWNER, ADMIN, FINANCE, READ_ONLY}) {
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
