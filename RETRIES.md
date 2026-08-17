# Retries

Retry automático só em cenários seguros — ver `SDK_CAPABILITY_SPEC.md` §8.

| Cenário | Retry? |
|---|---|
| Falha de conexão (reset, connect timeout) | Sempre |
| HTTP 429 | Sempre — respeita `Retry-After` real quando presente |
| HTTP 5xx | Só se a chamada for idempotente (GET, ou POST/DELETE com Idempotency-Key) |
| HTTP 400/401/403/404/409/422 | **Nunca** — são erros determinísticos, repetir não muda o resultado |

## Configuração

```java
var client = IshtaranClient.builder()
        .apiKey(apiKey)
        .environment(Environment.LOCAL)
        // (retry customizado ainda não exposto no builder público — usar defaults por ora)
        .build();
```

Defaults: até 2 tentativas adicionais (3 no total), backoff exponencial com jitter (base 200ms,
fator 2x, teto 5s). Ver `com.ishtaran.sdk.config.RetryPolicy`.

## Por que 5xx só reintenta com idempotência

Um 5xx genuíno pode significar que o servidor processou parcialmente o efeito antes de falhar
(ex.: debitou saldo mas não confirmou a resposta). Reintentar um `POST` de mutação sem
Idempotency-Key correria o risco de duplicar o efeito — o SDK nunca faz isso. `GET`s são
naturalmente seguros para retry (sem efeito colateral).
