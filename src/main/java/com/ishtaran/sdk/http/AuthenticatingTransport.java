package com.ishtaran.sdk.http;

import com.ishtaran.sdk.auth.BearerTokenHolder;

/**
 * Attaches {@code X-Api-Key} (when configured) and/or {@code Authorization: Bearer} (when a Member
 * login has already happened on this client instance, via {@link BearerTokenHolder}) — never the two
 * disguised as each other (rule from the brief: never fake a JWT as an API Key or vice versa).
 */
public final class AuthenticatingTransport implements HttpTransport {

    private static final String API_KEY_HEADER = "X-Api-Key";

    private final HttpTransport delegate;
    private final String apiKey;
    private final BearerTokenHolder bearerTokenHolder;

    public AuthenticatingTransport(HttpTransport delegate, String apiKey, BearerTokenHolder bearerTokenHolder) {
        this.delegate = delegate;
        this.apiKey = apiKey;
        this.bearerTokenHolder = bearerTokenHolder;
    }

    @Override
    public HttpResponse send(HttpRequest request) {
        if (apiKey != null && !apiKey.isBlank()) {
            request.header(API_KEY_HEADER, apiKey);
        }
        var token = bearerTokenHolder != null ? bearerTokenHolder.currentAccessToken() : null;
        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }
        return delegate.send(request);
    }
}
