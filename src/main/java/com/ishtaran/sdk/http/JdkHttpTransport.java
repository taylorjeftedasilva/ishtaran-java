package com.ishtaran.sdk.http;

import com.ishtaran.sdk.config.IshtaranClientConfig;
import com.ishtaran.sdk.error.NetworkError;
import com.ishtaran.sdk.error.TimeoutError;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * The only real implementation of {@link HttpTransport} — on top of {@code java.net.http} (JDK, zero
 * third-party dependency for transport). TLS verified by default; never disabled unless
 * {@link IshtaranClientConfig#allowInsecureTlsForLocalDevelopment()} is active (which is already
 * rejected in {@code build()} for any Environment other than LOCAL).
 */
public final class JdkHttpTransport implements HttpTransport {

    private final HttpClient client;
    private final String baseUrl;
    private final Duration requestTimeout;
    private final String userAgent;

    public JdkHttpTransport(IshtaranClientConfig config) {
        this.baseUrl = config.baseUrl();
        this.requestTimeout = config.requestTimeout();
        this.userAgent = config.userAgent();
        this.client = HttpClient.newBuilder()
                .connectTimeout(config.connectTimeout())
                .build();
    }

    @Override
    public HttpResponse send(HttpRequest request) {
        var builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + request.path()))
                .timeout(requestTimeout)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json");

        var bodyPublisher = request.body() != null
                ? java.net.http.HttpRequest.BodyPublishers.ofString(request.body())
                : java.net.http.HttpRequest.BodyPublishers.noBody();

        if (request.body() != null) {
            builder.header("Content-Type", "application/json");
        }

        switch (request.method()) {
            case GET -> builder.GET();
            case DELETE -> builder.DELETE();
            case POST -> builder.POST(bodyPublisher);
            case PUT -> builder.PUT(bodyPublisher);
            case PATCH -> builder.method("PATCH", bodyPublisher);
        }

        request.headers().forEach(builder::header);

        try {
            var response = client.send(builder.build(), java.net.http.HttpResponse.BodyHandlers.ofString());
            return new HttpResponse(response.statusCode(), response.headers().map(), response.body());
        } catch (HttpTimeoutException e) {
            throw new TimeoutError("Timeout calling " + request.method() + " " + request.path(), e);
        } catch (IOException e) {
            throw new NetworkError("Network failure calling " + request.method() + " " + request.path(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkError("Call interrupted: " + request.method() + " " + request.path(), e);
        }
    }
}
