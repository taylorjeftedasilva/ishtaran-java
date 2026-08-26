# Features

Derived from the real API contract (see the [API Reference](https://ishtaran.com/docs/api/ishtaran-api)). Core API: 100/100 real
operations (16/16 modules). Easy Mode: 100% (`receivePayment`, `withdraw`, `getBalance`,
`verifyWebhookSignature`). Cross-cutting: 100% (config, auth, errors, retry, idempotency,
pagination, forward-compatible enums, security/redaction, opt-in logging, safe `waitFor`,
validated Maven Central packaging).

Java is the **reference implementation** — TypeScript, Python, and Go match it with 100%
functional parity (same business-concept names, same defaults, same retry/idempotency/timeout
policy), differing only in each language's idiom (`client.withdrawals().quote(...)` in Java vs.
`client.withdrawals.quote(...)` in TypeScript/Python/Go).

## Self-custody and AccountHolders

Both shipped and at parity across all 4 languages: `client.wallets()`/`client.signingRequests()`
(local wallet generation/restoration, canonical-hash signing, `SigningRequest` submission — see
[README.md § Self-custody](../README.md#self-custody)) and `client.accountHolders()` (global
financial identity, `DEC-032` — see [README.md § What this SDK does](../README.md#what-this-sdk-does)).
