package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.InviteMemberResult;
import com.ishtaran.sdk.model.controlplane.MemberResponse;
import com.ishtaran.sdk.model.controlplane.TokenResult;
import com.ishtaran.sdk.model.enums.MemberRole;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Control Plane — {@code Members} (7 real routes, IdentityAccess). */
public final class MembersResource extends ApiResourceSupport {

    public MembersResource(HttpTransport transport) {
        super(transport);
    }

    public InviteMemberResult invite(UUID organizationId, String email, MemberRole role) {
        var body = toJson(Map.of("email", email, "role", role.rawValue()));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/members", body, false),
                InviteMemberResult.class);
    }

    /** Returns the real access token — the same mechanism {@code auth().login()} uses fills the client's session. */
    public TokenResult acceptInvite(String inviteToken, String password) {
        var body = toJson(Map.of("inviteToken", inviteToken, "password", password));
        return execute(HttpRequest.post("/v1/members/accept-invite", body, false), TokenResult.class);
    }

    public List<MemberResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/members"),
                new TypeReference<List<MemberResponse>>() {
                });
    }

    public void assignRole(UUID memberId, MemberRole newRole) {
        var body = toJson(Map.of("newRole", newRole.rawValue()));
        executeNoContent(HttpRequest.post("/v1/members/" + memberId + "/role", body, false));
    }

    public void suspend(UUID memberId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/members/" + memberId + "/suspend", body, false));
    }

    public void reactivate(UUID memberId) {
        executeNoContent(HttpRequest.post("/v1/members/" + memberId + "/reactivate", null, false));
    }

    public void remove(UUID memberId) {
        executeNoContent(HttpRequest.delete("/v1/members/" + memberId));
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
