# Error Handling

Every SDK error is an `IshtaranError` (unchecked) or subtype — see `SDK_CAPABILITY_SPEC.md` §6.

```
IshtaranError (base)
├── AuthenticationError       (401 — no code/detail, the real API doesn't send a body in this case)
├── AuthorizationError        (403 — same)
├── ValidationError           (400, code=VALIDATION_ERROR — message is a single string, never a per-field list)
├── NotFoundError             (404, code=NOT_FOUND)
├── ConflictError             (409 — several possible codes)
├── IdempotencyConflictError  (409, code=IDEMPOTENCY_KEY_CONFLICT — subtype of ConflictError)
├── RateLimitError            (429, code=RATE_LIMITED — exposes retryAfterSeconds())
├── NetworkError              (transport failure — no HTTP response)
├── TimeoutError              (connect/read timeout, or waitFor exceeding its deadline)
└── ApiError                  (fallback — any unmapped 4xx/5xx, preserves the raw status/code/detail)
```

## Basic usage

```java
try {
    var withdrawal = client.withdrawals().request(orgId, accountId, destinationId, assetNetworkId, amount, null);
} catch (ValidationError e) {
    log.warn("Validation failed: {}", e.getMessage()); // message is already the API's full string
} catch (RateLimitError e) {
    Thread.sleep(e.retryAfterSeconds() * 1000L); // or let automatic retry handle it
} catch (IshtaranError e) {
    log.error("Failure ({}): {}", e.httpStatus(), e.getMessage());
}
```

## Fields available on every instance

| Field | Description |
|---|---|
| `httpStatus()` | Real HTTP status (null on `NetworkError`/`TimeoutError`) |
| `code()` | Stable key (e.g. `VALIDATION_ERROR`) — null on 401/403 and transport failures |
| `requestId()` | Always null today — the real API has no request/correlation ID mechanism (real documented gap, see `SDK_CAPABILITY_SPEC.md` §12.1) |
| `details()` | Raw error body, when present |
| `retryable()` | Whether this error is a candidate for automatic retry (see `RETRIES.md`) |

## Why 401/403 have no `code`/`detail`

Confirmed in source code: no backend `AuthenticationHandler` registers a custom
`OnChallenge`/`OnForbidden` — ASP.NET Core's authentication/authorization middleware responds
with an empty body before it even reaches the `DomainExceptionHandler` that produces the normal
`ProblemDetails`. The SDK never tries to parse a body that doesn't exist.
