package com.ishtaran.sdk.config;

import java.time.Duration;

/**
 * Política de retry — ver SDK_CAPABILITY_SPEC.md §8. Retry só em falha de conexão, 429 (respeitando
 * {@code Retry-After}), e 5xx quando a chamada tem Idempotency-Key. Nunca em 4xx determinístico
 * (400/401/403/404/409/422).
 *
 * @param maxRetries       tentativas adicionais além da primeira (default 2 → até 3 tentativas totais)
 * @param baseBackoff      atraso base do backoff exponencial (default 200ms)
 * @param backoffMultiplier fator multiplicativo por tentativa (default 2.0)
 * @param maxBackoff       teto do backoff, antes do jitter (default 5s)
 */
public record RetryPolicy(int maxRetries, Duration baseBackoff, double backoffMultiplier, Duration maxBackoff) {

    public static RetryPolicy defaults() {
        return new RetryPolicy(2, Duration.ofMillis(200), 2.0, Duration.ofSeconds(5));
    }

    public static RetryPolicy disabled() {
        return new RetryPolicy(0, Duration.ZERO, 1.0, Duration.ZERO);
    }

    public RetryPolicy {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries não pode ser negativo");
        }
    }
}
