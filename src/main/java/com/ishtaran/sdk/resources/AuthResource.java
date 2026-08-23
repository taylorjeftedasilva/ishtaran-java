package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.auth.BearerTokenHolder;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.controlplane.SignUpResponse;
import com.ishtaran.sdk.model.controlplane.TokenResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.Map;

/**
 * Control Plane — {@code /v1/auth/*} (5 real routes). {@link #login} automatically fills the client's
 * {@link BearerTokenHolder}, used by {@code AuthenticatingTransport} on every subsequent Control
 * Plane call — the consumer never needs to pass the token along manually.
 */
public final class AuthResource extends ApiResourceSupport {

    private final BearerTokenHolder bearerTokenHolder;

    public AuthResource(HttpTransport transport, BearerTokenHolder bearerTokenHolder) {
        super(transport);
        this.bearerTokenHolder = bearerTokenHolder;
    }

    public TokenResult login(String email, String password) {
        var body = toJson(Map.of("email", email, "password", password));
        var result = execute(HttpRequest.post("/v1/auth/login", body, false), TokenResult.class);
        if (result != null && result.success() && result.accessToken() != null) {
            bearerTokenHolder.set(result.accessToken());
        }
        return result;
    }

    public SignUpResponse signUp(String organizationName, String email, String password) {
        var body = toJson(Map.of("organizationName", organizationName, "email", email, "password", password));
        var result = execute(HttpRequest.post("/v1/auth/signup", body, false), SignUpResponse.class);
        if (result != null && result.token() != null && result.token().success() && result.token().accessToken() != null) {
            bearerTokenHolder.set(result.token().accessToken());
        }
        return result;
    }

    public TokenResult refresh(String refreshToken) {
        var body = toJson(Map.of("refreshToken", refreshToken));
        var result = execute(HttpRequest.post("/v1/auth/refresh", body, false), TokenResult.class);
        if (result != null && result.success() && result.accessToken() != null) {
            bearerTokenHolder.set(result.accessToken());
        }
        return result;
    }

    public void requestPasswordReset(String email) {
        var body = toJson(Map.of("email", email));
        executeNoContent(HttpRequest.post("/v1/auth/password-reset/request", body, false));
    }

    public void confirmPasswordReset(String resetToken, String newPassword) {
        var body = toJson(Map.of("resetToken", resetToken, "newPassword", newPassword));
        executeNoContent(HttpRequest.post("/v1/auth/password-reset/confirm", body, false));
    }

    /** No stateful HTTP call — allows testing/clearing the client's local session. */
    public void logout() {
        bearerTokenHolder.clear();
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
