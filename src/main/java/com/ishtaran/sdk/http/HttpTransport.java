package com.ishtaran.sdk.http;

/**
 * Transport abstraction — the only real implementation is {@link JdkHttpTransport}, but tests use
 * a fake in-memory implementation, with no network (explicit requirement from the SDK Program brief).
 */
public interface HttpTransport {
    HttpResponse send(HttpRequest request);
}
