package com.ishtaran.sdk.http;

import com.ishtaran.sdk.util.Redactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opt-in logging (only active when {@code IshtaranClientConfig.enableLogging(true)}) — never logs
 * {@code Authorization}/{@code X-Api-Key} in plain text (central redaction via {@link Redactor}),
 * never logs the raw body (may contain a webhook secret/API Key in a creation response — only
 * method/path/status/duration). SLF4J facade — no mandatory implementation, the consumer chooses
 * the log backend.
 */
public final class LoggingTransport implements HttpTransport {

    private static final Logger LOG = LoggerFactory.getLogger("com.ishtaran.sdk.http");

    private final HttpTransport delegate;

    public LoggingTransport(HttpTransport delegate) {
        this.delegate = delegate;
    }

    @Override
    public HttpResponse send(HttpRequest request) {
        long start = System.nanoTime();
        LOG.debug("--> {} {} headers={}", request.method(), request.path(), redactedHeaders(request));
        try {
            var response = delegate.send(request);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            LOG.debug("<-- {} {} status={} ({} ms)", request.method(), request.path(), response.status(), elapsedMs);
            return response;
        } catch (RuntimeException e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            LOG.debug("<-- {} {} FAILED: {} ({} ms)", request.method(), request.path(), e.getClass().getSimpleName(), elapsedMs);
            throw e;
        }
    }

    /** Package-private (not {@code private}) only to allow direct testing of redaction, without reflection. */
    String redactedHeaders(HttpRequest request) {
        var sb = new StringBuilder("{");
        request.headers().forEach((name, value) -> {
            sb.append(name).append('=');
            sb.append(Redactor.isSensitiveHeader(name) ? Redactor.mask(value) : value);
            sb.append(", ");
        });
        sb.append("}");
        return sb.toString();
    }
}
