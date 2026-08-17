package com.ishtaran.sdk.http;

/**
 * Abstração de transporte — a única implementação real é {@link JdkHttpTransport}, mas testes usam
 * uma implementação falsa in-memory, sem rede (requisito explícito do brief do SDK Program).
 */
public interface HttpTransport {
    HttpResponse send(HttpRequest request);
}
