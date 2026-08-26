# Retries

Automatic retry only in safe scenarios — see `SDK_CAPABILITY_SPEC.md` §8.

| Scenario | Retry? |
|---|---|
| Connection failure (reset, connect timeout) | Always |
| HTTP 429 | Always — respects the real `Retry-After` when present |
| HTTP 5xx | Only if the call is idempotent (GET, or POST/DELETE with Idempotency-Key) |
| HTTP 400/401/403/404/409/422 | **Never** — deterministic errors, retrying won't change the result |

## Configuration

```java
var client = IshtaranClient.builder()
        .apiKey(apiKey)
        .environment(Environment.LOCAL)
        // (custom retry not yet exposed on the public builder -- use the defaults for now)
        .build();
```

Defaults: up to 2 additional attempts (3 total), exponential backoff with jitter (base 200ms,
factor 2x, cap 5s). See `com.ishtaran.sdk.config.RetryPolicy`.

## Why 5xx only retries with idempotency

A genuine 5xx can mean the server partially processed the effect before failing (e.g. debited a
balance but didn't confirm the response). Retrying a mutation `POST` without an Idempotency-Key
would risk duplicating the effect — the SDK never does that. `GET`s are naturally safe to retry
(no side effect).
