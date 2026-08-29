# Changelog

Follows [SemVer](https://semver.org/). This is a **Development Preview** — 0.x versions may
still change before a stable 1.0.0.

## [Unreleased]

- Added `client.executionDestinations().register(organizationId, accountId, assetNetworkId,
  address)` (`POST /v1/organizations/{organizationId}/execution-destinations`) -- registers the
  real on-chain address a beneficiary `Account` receives funds at, for a given `AssetNetwork`.
  Required before a `Settlement` involving that Account can execute under SelfCustody (`DEC-037`):
  `settlements().executeSettlement` now resolves every beneficiary's (and the Platform Fee's)
  destination before building a `SigningRequest` and fails fast, before any signing/broadcast, if
  none is registered. First-registration-wins -- a second call for the same `accountId`+
  `assetNetworkId` is rejected, never silently overwritten. Also added
  `SettlementResponse.signingRequestId()` -- populated once a `Settlement` moves to SelfCustody
  execution; fetch it with `client.signingRequests().get(signingRequestId)` to sign locally. Found
  and fixed while closing out the real on-chain execution path for the Mercatto Business Case --
  the backend's `ISettlementExecutionStrategy` split (`SelfCustodySettlementExecutionStrategy` vs.
  legacy `ManagedCustodySettlementExecutionStrategy`) was already implemented, but no SDK exposed
  the new `ExecutionDestination` resource or the `signingRequestId` needed to actually complete a
  real Settlement end to end. No breaking change -- both are additive.
- **Breaking:** `SettlementsResource.executeSettlement(UUID transactionId, String idempotencyKey)`
  is now `executeSettlement(UUID transactionId, BigDecimal amount, String idempotencyKey)` -- a
  new `amount` parameter was inserted before `idempotencyKey`. Enables Partial Settlement
  (`BL-STL-008`, activated 2026-08-26): `null` settles the full remaining reserved amount
  (unchanged default), or pass a value to settle exactly that amount -- callable multiple times on
  the same Transaction until the remaining balance reaches zero, each call computing its own
  Platform Fee on its own gross slice. Found and fixed while building the Mercatto marketplace
  Business Case: the platform's domain/Application layer already supported this per-call `Amount`
  since `DEC-019`, but the HTTP contract never exposed it -- a real, deliberate MVP deferral
  (`BL-STL-008`, Pós-MVP) now activated by explicit product decision.
- Fixed a real bug in the platform's Ledger module, also found via the Mercatto Business Case:
  `BR-BAL-005` (Asset Network `MinAmount`/`MaxAmount`) was being enforced on every individual
  Ledger Entry of every internal record command -- including Settlement's Fee/Split postings --
  instead of only on the Gross Amount of a Reserve/Release operation, as the platform's own Ledger
  spec always documented. No SDK-visible API change -- documented here because it directly affects
  which amounts a real `executeSettlement()` call can now succeed with.

## [0.1.2] — 2026-08-25

- Fixed a real bug, found while building example 14: `AuthResource.signUp(...)` never sent the
  `Idempotency-Key` header `POST /v1/auth/signup` requires — every real call failed with `400
  IDEMPOTENCY_KEY_REQUIRED`. Self-service onboarding via this SDK never actually worked before
  this fix. New overload `signUp(organizationName, email, password, idempotencyKey)`; the existing
  3-arg overload now auto-generates one, same convention as `OrganizationsResource.create`. No
  breaking change.
- Added `examples/Example14MarketplaceJourney.java`: a full marketplace payment, verified live
  against the real Sandbox (self-service signup, a self-custody execution wallet, a seller
  `AccountHolder`, a buyer Payment Intent, and a locally signed payout) -- connects several
  existing examples into one closed cycle.
- Fixed `examples/pom.xml` and `samples/maven-consumer/pom.xml`, both still pinned to the
  pre-rename `1.0.0-SNAPSHOT` -- silently building against a stale cached local install instead of
  the current source. Now `0.1.2`.
- `CORE_API.md` corrected: documents `AccountHolders`/self-custody resources it omitted, notes
  that `accounts().authorizeApplication`/`freeze`/`unfreeze`/`close`/`revokeRelationship` reject
  an API Key and require a Member session (found live, undocumented until now), and that a
  Transaction reserves itself automatically once its deposit is confirmed -- no `reserve()` call
  needed or valid in that path.

## [0.1.1] — 2026-08-25

- `Environment.SANDBOX` now resolves to the real public Sandbox (`https://sandbox-api.ishtaran.com`,
  the canonical domain live since 2026-08-25 — Cloud Run Domain Mapping) by default — no explicit
  `.baseUrl(...)` needed, though one always overrides it. Previously it required an explicit
  `.baseUrl(...)` and threw `IllegalStateException` otherwise. `Environment.PRODUCTION` is
  unchanged (still requires an explicit `.baseUrl(...)`). Backward compatible.
- Fixed: `UserAgent.SDK_VERSION` (sent as `ishtaran-java/<version>` on every request) was still
  hardcoded to the pre-release placeholder `1.0.0-SNAPSHOT`, misreporting the actual published
  version. Now `0.1.1`, matching `pom.xml`.

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
