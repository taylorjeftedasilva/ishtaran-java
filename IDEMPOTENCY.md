# Idempotency

Two real, different mechanisms — never a single pattern assumed (see `SDK_CAPABILITY_SPEC.md`
§9).

## Body field (`idempotencyKey`) — every financial endpoint

`transactions().create(...)`, `deposits().createPaymentIntent(...)`,
`settlements().executeSettlement(...)`, `refunds().executeRefund(...)`,
`withdrawals().request(...)`, `events().ingest(...)`:

```java
// Omitted -- the SDK auto-generates a UUID v4.
var txn = client.transactions().create(orgId, appId, null, assetNetworkId, amount, participants, null);

// Explicit -- never overwritten by the SDK.
var txn2 = client.transactions().create(orgId, appId, null, assetNetworkId, amount, participants, "my-key-123");
```

## `Idempotency-Key` header — only 2 real endpoints

`organizations().create(...)` and `organizations().createApplication(...)` — confirmed in source
code, the only 2 places in the entire backend that use `[FromHeader(Name =
"Idempotency-Key")]` instead of a body field. Same auto-generation/explicit-override policy,
just via header:

```java
var org = client.organizations().create("Acme Inc", null); // header auto-generated
```

No other `OrganizationTenancy` endpoint (Environments, ApiKeys) has any idempotency protection —
a real API gap, not something the SDK should pretend to fix.

## Resending with the same key

Resending the same key with the same payload is safe (the API treats it as a replay). Resending
the same key with a different payload results in `IdempotencyConflictError` (409,
`IDEMPOTENCY_KEY_CONFLICT`) — the SDK propagates this typed error, never hiding it.

## Automatic retry never generates a new key

When `RetryingTransport` retries a call with an Idempotency-Key (see `RETRIES.md`), it resends
exactly the same already-built request — the same key from the first attempt, never a new one
generated per attempt (that would break the idempotency guarantee).
