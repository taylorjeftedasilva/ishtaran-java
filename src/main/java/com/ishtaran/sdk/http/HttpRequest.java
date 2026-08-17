package com.ishtaran.sdk.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Requisição interna, independente de biblioteca de transporte — nunca vaza {@code java.net.http}
 * (ou qualquer lib HTTP concreta) na superfície pública, permitindo testar {@code resources/*} com
 * um {@link HttpTransport} falso, sem rede.
 */
public final class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final String body;
    private final boolean idempotent;

    private HttpRequest(HttpMethod method, String path, String body, boolean idempotent) {
        this.method = method;
        this.path = path;
        this.body = body;
        this.idempotent = idempotent;
    }

    public static HttpRequest get(String path) {
        return new HttpRequest(HttpMethod.GET, path, null, true);
    }

    public static HttpRequest post(String path, String jsonBody, boolean idempotent) {
        return new HttpRequest(HttpMethod.POST, path, jsonBody, idempotent);
    }

    public static HttpRequest delete(String path) {
        return new HttpRequest(HttpMethod.DELETE, path, null, false);
    }

    public HttpRequest header(String name, String value) {
        if (value != null) {
            headers.put(name, value);
        }
        return this;
    }

    public HttpMethod method() {
        return method;
    }

    public String path() {
        return path;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String body() {
        return body;
    }

    /** Chamadas com Idempotency-Key (ou GET, naturalmente idempotente) podem ter 5xx retried com segurança (§8 do Capability Spec). */
    public boolean idempotent() {
        return idempotent;
    }
}
