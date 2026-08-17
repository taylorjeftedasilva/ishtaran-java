package com.ishtaran.sdk.http;

import com.ishtaran.sdk.util.Redactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging opt-in (só ativo quando {@code IshtaranClientConfig.enableLogging(true)}) — nunca loga
 * {@code Authorization}/{@code X-Api-Key} em texto puro (redação central via {@link Redactor}),
 * nunca loga o corpo bruto (pode conter secret de webhook/API Key na resposta de criação — só
 * método/path/status/duração). Fachada SLF4J — sem implementação obrigatória, o consumidor escolhe
 * o backend de log.
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

    /** Package-private (não {@code private}) só para permitir teste direto da redação, sem reflection. */
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
