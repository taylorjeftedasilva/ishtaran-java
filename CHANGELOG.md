# Changelog

Follows [SemVer](https://semver.org/). This is a **Development Preview** — 0.x versions may
still change before a stable 1.0.0.

## [Unreleased]

- `Environment.SANDBOX` now resolves to the real public Sandbox (`https://sandbox-api.ishtaran.com`,
  the canonical domain live since 2026-08-25 — Cloud Run Domain Mapping) by default — no explicit
  `.baseUrl(...)` needed, though one always overrides it. Previously it required an explicit
  `.baseUrl(...)` and threw `IllegalStateException` otherwise. `Environment.PRODUCTION` is
  unchanged (still requires an explicit `.baseUrl(...)`). Backward compatible — not yet published
  to Maven Central.
- Fixed: `UserAgent.SDK_VERSION` (sent as `ishtaran-java/<version>` on every request) was still
  hardcoded to the pre-release placeholder `1.0.0-SNAPSHOT`, misreporting the actual published
  version. Now `0.1.0`, matching `pom.xml` and the Maven Central release. Not yet published to
  Maven Central.

## [0.1.0] — 2026-08-24

First public release, published on Maven Central (`com.ishtaran:ishtaran-java:0.1.0`). Builds on
the `1.0.0-SNAPSHOT` work below, plus:

### Added since `1.0.0-SNAPSHOT`

- Self-custody wallet generation and restoration (`WalletFactory.generate`/`restore`,
  BIP39/BIP32/BIP44).
- Tron address derivation from the public account key only (`TronAddress.derive`).
- Local canonical-hash signing (`InMemorySigner`), documented as unsafe for Production —
  implement your own `Signer` against a Vault/KMS/HSM for any real deployment.
- `client.wallets()`/`client.signingRequests()` — the real `ExecutionCustody` HTTP routes end to
  end.
- `client.accountHolders()` — self-service for the financial holder's global identity.
- License: Apache License 2.0.

### Known, still pending

- Production blockchain execution is not available yet.

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
