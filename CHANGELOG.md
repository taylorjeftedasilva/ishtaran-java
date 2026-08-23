# Changelog

Follows [SemVer](https://semver.org/). Not yet published (Maven Central) — versions below reflect
the state of local development.

## [1.0.0-SNAPSHOT] — 2026-08-17

First implementation — Java is the reference language of the Ishtaran Official SDK Program.

### Added

- Central client (`IshtaranClient`, builder, `IshtaranClientConfig`).
- Complete Core API — 16 modules, 93 real operations (Organizations, Applications, Environments,
  ApiKeys, Members, AssetNetworkCatalog, Accounts, Transactions, Deposits, Ledger, Settlements,
  Refunds, Withdrawals, WorkflowRules/EventTypes/Events, Sandbox, WebhookEndpoints/Deliveries).
- Easy Mode — `receivePayment`/`getPayment`/`waitForPayment`, `withdraw`, `getBalance`,
  `verifyWebhookSignature`.
- `X-Api-Key` + Member JWT authentication (`auth().login()`).
- Complete `IshtaranError` hierarchy (10 subtypes) with mapping faithful to the real
  `ProblemDetails`.
- Safe retry with backoff+jitter, respecting `Retry-After`.
- Idempotency (body field AND header, depending on the real endpoint) with auto-generation.
- Real pagination (`PageIterator`, 2 endpoints) + plain lists for the rest.
- Enums with `UNKNOWN` fallback preserving the raw value (real forward-compatibility).
- `WebhookSignatureVerifier` (HMAC-SHA256, constant-time, replay tolerance).
- Opt-in logging with central secret redaction.
- Maven packaging validated via a real dry run (consumption by sample Maven and Gradle projects).

### Known, still pending

- Complete example documentation (`examples/`) — see `SECURITY_REVIEW.md` and
  `JAVA_SDK_CHECKPOINT.md` for the exact state.
- Actual publication to Maven Central — blocked by a pending licensing decision.
