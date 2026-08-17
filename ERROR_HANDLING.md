# Error Handling

Todo erro do SDK é um `IshtaranError` (unchecked) ou subtipo — ver `SDK_CAPABILITY_SPEC.md` §6.

```
IshtaranError (base)
├── AuthenticationError       (401 — sem code/detail, a API real não envia corpo neste caso)
├── AuthorizationError        (403 — idem)
├── ValidationError           (400, code=VALIDATION_ERROR — mensagem é 1 string, nunca lista por campo)
├── NotFoundError             (404, code=NOT_FOUND)
├── ConflictError             (409 — vários code possíveis)
├── IdempotencyConflictError  (409, code=IDEMPOTENCY_KEY_CONFLICT — subtipo de ConflictError)
├── RateLimitError            (429, code=RATE_LIMITED — expõe retryAfterSeconds())
├── NetworkError              (falha de transporte — sem resposta HTTP)
├── TimeoutError              (connect/read timeout, ou waitFor excedendo o prazo)
└── ApiError                  (fallback — qualquer 4xx/5xx não mapeado, preserva status/code/detail brutos)
```

## Uso básico

```java
try {
    var withdrawal = client.withdrawals().request(orgId, accountId, destinationId, assetNetworkId, amount, null);
} catch (ValidationError e) {
    log.warn("Validação falhou: {}", e.getMessage()); // mensagem já é a string completa da API
} catch (RateLimitError e) {
    Thread.sleep(e.retryAfterSeconds() * 1000L); // ou deixe o retry automático cuidar disso
} catch (IshtaranError e) {
    log.error("Falha ({}): {}", e.httpStatus(), e.getMessage());
}
```

## Campos disponíveis em toda instância

| Campo | Descrição |
|---|---|
| `httpStatus()` | Status HTTP real (nulo em `NetworkError`/`TimeoutError`) |
| `code()` | Chave estável (ex. `VALIDATION_ERROR`) — nulo em 401/403 e falhas de transporte |
| `requestId()` | Sempre nulo hoje — a API real não tem mecanismo de request/correlation ID (lacuna real documentada, ver `SDK_CAPABILITY_SPEC.md` §12.1) |
| `details()` | Corpo bruto do erro, quando existir |
| `retryable()` | Se este erro é candidato a retry automático (ver `RETRIES.md`) |

## Por que 401/403 não têm `code`/`detail`

Confirmado em código-fonte: nenhum `AuthenticationHandler` do backend registra um
`OnChallenge`/`OnForbidden` customizado — o middleware de autenticação/autorização do ASP.NET Core
responde com um corpo vazio antes mesmo de chegar no `DomainExceptionHandler` que produz o
`ProblemDetails` normal. O SDK nunca tenta parsear um corpo que não existe.
