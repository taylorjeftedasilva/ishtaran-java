package com.ishtaran.sdk.auth;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the Member access token (after {@code auth().login(...)}) — thread-safe, mutable within
 * a single {@code IshtaranClient} instance (the client is reusable across threads, see
 * SDK_CAPABILITY_SPEC.md/brief "thread-safe where reasonable"). Never logs the token.
 */
public final class BearerTokenHolder {

    private final AtomicReference<String> accessToken = new AtomicReference<>();

    public void set(String token) {
        accessToken.set(token);
    }

    public String currentAccessToken() {
        return accessToken.get();
    }

    public void clear() {
        accessToken.set(null);
    }
}
