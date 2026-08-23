package com.ishtaran.sdk.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal request, independent of any transport library — never leaks {@code java.net.http}
 * (or any concrete HTTP lib) into the public surface, allowing {@code resources/*} to be tested with
 * a fake {@link HttpTransport}, with no network.
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

    /** Calls with an Idempotency-Key (or GET, naturally idempotent) can have 5xx safely retried (§8 of the Capability Spec). */
    public boolean idempotent() {
        return idempotent;
    }
}
