package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.AccountResponse;
import com.ishtaran.sdk.model.dataplane.CreateAccountHolderInvitationResult;
import com.ishtaran.sdk.model.dataplane.CreateAccountResult;
import com.ishtaran.sdk.model.dataplane.OrganizationAccountResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code Accounts} (9 real routes, DEC-032). */
public final class AccountsResource extends ApiResourceSupport {

    public AccountsResource(HttpTransport transport) {
        super(transport);
    }

    public CreateAccountResult create(UUID organizationId, String externalId) {
        var body = toJson(Map.of("externalId", externalId));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/accounts", body, false),
                CreateAccountResult.class);
    }

    /** Returns this Organization's link (Relationship) to each Account — never the Account in isolation (DEC-032). */
    public List<OrganizationAccountResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/accounts"),
                new TypeReference<List<OrganizationAccountResponse>>() {
                });
    }

    public AccountResponse get(UUID accountId) {
        return execute(HttpRequest.get("/v1/accounts/" + accountId), AccountResponse.class);
    }

    /**
     * DEC-032 — route nested under {@code organizationId} (previously {@code /v1/accounts/{accountId}/authorize-application}):
     * the backend revalidates the Relationship (organizationId, Account.AccountHolderId) internally, the
     * nested route only simplifies resolving the caller's ownership.
     */
    public void authorizeApplication(UUID organizationId, UUID accountId, UUID applicationId) {
        var body = toJson(Map.of("applicationId", applicationId));
        executeNoContent(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/accounts/" + accountId + "/authorize-application", body, false));
    }

    public void freeze(UUID accountId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/accounts/" + accountId + "/freeze", body, false));
    }

    public void unfreeze(UUID accountId) {
        executeNoContent(HttpRequest.post("/v1/accounts/" + accountId + "/unfreeze", null, false));
    }

    public void close(UUID accountId) {
        executeNoContent(HttpRequest.delete("/v1/accounts/" + accountId));
    }

    /**
     * DEC-032/BR-HLD-005 — issues an invitation for an AccountHolder to relate to this Organization.
     * {@code plainTextToken} exists only in this response, a single time — treat it as a secret.
     */
    public CreateAccountHolderInvitationResult createAccountHolderInvitation(UUID organizationId, String externalId) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("externalId", externalId);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/account-holder-invitations", body, false),
                CreateAccountHolderInvitationResult.class);
    }

    /** DEC-032/BR-ACC-008 — never deletes the AccountHolder/Account, only removes this Organization's authorization. */
    public void revokeRelationship(UUID organizationId, UUID relationshipId) {
        executeNoContent(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/relationships/" + relationshipId + "/revoke", null, false));
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
