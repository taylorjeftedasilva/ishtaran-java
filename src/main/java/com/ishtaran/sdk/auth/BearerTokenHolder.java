package com.ishtaran.sdk.auth;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Guarda o access token de Member (após {@code auth().login(...)}) — thread-safe, mutável dentro de
 * uma instância de {@code IshtaranClient} (client é reusável entre threads, ver
 * SDK_CAPABILITY_SPEC.md/brief "thread-safe onde razoável"). Nunca loga o token.
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
