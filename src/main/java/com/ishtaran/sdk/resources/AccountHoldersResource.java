package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.auth.BearerTokenHolder;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.AccountHolderResponse;
import com.ishtaran.sdk.model.dataplane.AccountHolderTokenResult;
import com.ishtaran.sdk.model.dataplane.ClaimAccountHolderInvitationResult;
import com.ishtaran.sdk.model.dataplane.SignUpAndClaimAccountHolderInvitationResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.Map;

/**
 * DEC-032 — self-service, global {@code AccountHolder} identity ({@code /v1/account-holders/*}),
 * authenticated by {@code AccountHolderJwtScheme} — its own key/token, never shared with the
 * {@link BearerTokenHolder} of {@link AuthResource} (Member) nor with the Organization's
 * {@code X-Api-Key}. Built on its own transport (see {@code IshtaranClient}) precisely so
 * that the two tokens never mix within the same client instance.
 */
public final class AccountHoldersResource extends ApiResourceSupport {

    private final BearerTokenHolder accountHolderTokenHolder;

    public AccountHoldersResource(HttpTransport transport, BearerTokenHolder accountHolderTokenHolder) {
        super(transport);
        this.accountHolderTokenHolder = accountHolderTokenHolder;
    }

    public AccountHolderTokenResult signUp(String email, String password) {
        var body = toJson(Map.of("email", email, "password", password));
        var result = execute(HttpRequest.post("/v1/account-holders/signup", body, false), AccountHolderTokenResult.class);
        if (result != null && result.success() && result.accessToken() != null) {
            accountHolderTokenHolder.set(result.accessToken());
        }
        return result;
    }

    public AccountHolderTokenResult login(String email, String password) {
        var body = toJson(Map.of("email", email, "password", password));
        var result = execute(HttpRequest.post("/v1/account-holders/login", body, false), AccountHolderTokenResult.class);
        if (result != null && result.success() && result.accessToken() != null) {
            accountHolderTokenHolder.set(result.accessToken());
        }
        return result;
    }

    /** Requires an active AccountHolder session ({@link #signUp}/{@link #login} already called on this client instance, or {@link #setAccessToken}). */
    public AccountHolderResponse me() {
        return execute(HttpRequest.get("/v1/account-holders/me"), AccountHolderResponse.class);
    }

    /** Requires an active AccountHolder session — claims an invitation from a NEW Organization for the already-authenticated identity (BR-HLD-006, reuses the existing Account, never duplicates). */
    public ClaimAccountHolderInvitationResult claimInvitation(String plainTextToken) {
        var body = toJson(Map.of("plainTextToken", plainTextToken));
        return execute(HttpRequest.post("/v1/account-holders/invitations/claim", body, false), ClaimAccountHolderInvitationResult.class);
    }

    /** No prior authentication — creates the identity and claims the invitation atomically (holder never seen before). */
    public SignUpAndClaimAccountHolderInvitationResult signUpAndClaimInvitation(String plainTextToken, String email, String password) {
        var body = toJson(Map.of("plainTextToken", plainTextToken, "email", email, "password", password));
        var result = execute(HttpRequest.post("/v1/account-holders/invitations/signup-and-claim", body, false),
                SignUpAndClaimAccountHolderInvitationResult.class);
        if (result != null && result.success() && result.token() != null && result.token().accessToken() != null) {
            accountHolderTokenHolder.set(result.token().accessToken());
        }
        return result;
    }

    /** Fills the session manually (e.g. a token obtained in a previous process) — never need to call signUp/login again on this instance. */
    public void setAccessToken(String accessToken) {
        accountHolderTokenHolder.set(accessToken);
    }

    /** No HTTP call — clears the local AccountHolder session (never affects the Organization's Member/API Key session). */
    public void logout() {
        accountHolderTokenHolder.clear();
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
