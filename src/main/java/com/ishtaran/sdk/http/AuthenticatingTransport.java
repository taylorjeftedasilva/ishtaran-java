package com.ishtaran.sdk.http;

import com.ishtaran.sdk.auth.BearerTokenHolder;

/**
 * Anexa {@code X-Api-Key} (quando configurado) e/ou {@code Authorization: Bearer} (quando um login
 * de Member já ocorreu nesta instância de client, via {@link BearerTokenHolder}) — nunca os dois
 * disfarçados um do outro (regra do brief: nunca falsificar JWT como API Key nem vice-versa).
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
