package com.ishtaran.sdk.http;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Transporte em memória, sem rede — usado por todo teste de {@code resources/*}/retry/erro. Existe
 * exatamente para cumprir o requisito do brief de uma abstração de HTTP testável sem rede real.
 */
public final class FakeHttpTransport implements HttpTransport {

    private final Deque<Function<HttpRequest, HttpResponse>> queuedResponders = new ArrayDeque<>();
    private final List<HttpRequest> received = new ArrayList<>();
    private Function<HttpRequest, HttpResponse> defaultResponder;

    public FakeHttpTransport enqueue(HttpResponse response) {
        queuedResponders.add(r -> response);
        return this;
    }

    public FakeHttpTransport enqueueThrow(RuntimeException exception) {
        queuedResponders.add(r -> {
            throw exception;
        });
        return this;
    }

    public FakeHttpTransport respondAlways(Function<HttpRequest, HttpResponse> responder) {
        this.defaultResponder = responder;
        return this;
    }

    @Override
    public HttpResponse send(HttpRequest request) {
        received.add(request);
        var next = queuedResponders.poll();
        if (next != null) {
            return next.apply(request);
        }
        if (defaultResponder != null) {
            return defaultResponder.apply(request);
        }
        throw new IllegalStateException("Nenhuma resposta configurada em FakeHttpTransport");
    }

    public List<HttpRequest> received() {
        return received;
    }

    public int requestCount() {
        return received.size();
    }

    public static HttpResponse json(int status, String body) {
        return new HttpResponse(status, Map.of(), body);
    }

    public static HttpResponse jsonWithHeaders(int status, String body, Map<String, List<String>> headers) {
        return new HttpResponse(status, headers, body);
    }
}
