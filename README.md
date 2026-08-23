# Ishtaran Java SDK

Official Java SDK for the [Ishtaran API](https://ishtaran.com) — a programmable financial
platform (virtual accounts, conditional release flows, settlements and on-chain withdrawals).

**Status:** reference implementation of the [Ishtaran Official SDK Program](../../SDK_CAPABILITY_SPEC.md)
(Java → TypeScript → Python → Go). See [`JAVA_SDK_CHECKPOINT.md`](../../JAVA_SDK_CHECKPOINT.md) at
the repository root for the exact completion state.

## Two layers, same backend

- **Easy Mode** — `client.receivePayment(...)`, `client.withdraw(...)`, `client.getBalance(...)`,
  `client.verifyWebhookSignature(...)`: fast composition for common integrations, without needing
  to understand the whole API surface. Never duplicates business logic — only combines Core
  calls.
- **Core API** — `client.accounts()`, `client.transactions()`, `client.withdrawals()`, etc.:
  granular access to exactly the same 90 real API endpoints (see
  [`SDK_FEATURE_MATRIX.md`](../../SDK_FEATURE_MATRIX.md)), with nothing invented beyond what the
  real API exposes.
- **AccountHolders** — `client.accountHolders()`: self-service for the financial holder's global
  identity (`DEC-032`) — `signUp`/`login`/`me`/`claimInvitation`/`signUpAndClaimInvitation`.
  Isolated session: never shares a token with `client.auth()` (Member) nor with the
  Organization's API Key within the same client instance.

## Self-custody wallet & signing (new, `SPEC-021`)

`com.ishtaran.sdk.wallet` / `com.ishtaran.sdk.signing` — generate or restore a BIP39/BIP32/BIP44
wallet locally, derive Tron addresses from the public account key only, and sign a leg's canonical
hash. **The private key, seed, and mnemonic never leave this code and are never sent to Ishtaran.**

```java
var generated = WalletFactory.generate(); // 24-word mnemonic, back it up now — it is shown only once
var address = TronAddress.derive(generated.wallet().accountExtendedPublicKey(), 0);
var signature = generated.signer().sign(0, canonicalHash); // canonicalHash: see CanonicalHash.compute(...)
```

`InMemorySigner` (the reference `Signer` implementation returned by `WalletFactory`) keeps the
account private key in plain process memory — **documented as unsafe for Production.** Implement
`Signer` yourself against a Vault/KMS/HSM/OS keychain for any real deployment; the interface never
mandates a specific backend.

`client.wallets()` / `client.signingRequests()` talk to the real `ExecutionCustody` HTTP routes —
register the wallet's public key, allocate a deposit address, create a `SigningRequest`, sign the
canonical hash it returns, and submit it back:

```java
var registered = client.wallets().register(applicationId, networkId, DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT,
        generated.wallet().accountExtendedPublicKey(), idempotencyKey);
var allocated = client.wallets().allocateDepositAddress(applicationId, networkId);
var created = client.signingRequests().create(environmentId, registered.walletId(), allocated.derivationReference(),
        originReference, assetNetworkId, allocated.address(), legs, OffsetDateTime.now(ZoneOffset.UTC).plusHours(1), idempotencyKey);
var signingRequest = client.signingRequests().get(created.signingRequestId());
for (var leg : signingRequest.legs()) {
    var signature = generated.signer().sign(allocated.derivationReference(), HexFormat.of().parseHex(leg.canonicalHash()));
    client.signingRequests().submitSignedTransaction(created.signingRequestId(), leg.executionLegId(),
            leg.canonicalHash(), HexFormat.of().withUpperCase().formatHex(signature));
}
```

See [`examples/Example13SelfCustodySigning.java`](../../sdks/java/examples/src/main/java/com/ishtaran/examples/Example13SelfCustodySigning.java)
for the full runnable flow — proved end to end against a real Sandbox (real broadcast confirmed).

## Installation

Not yet published on Maven Central (licensing decision pending — see
[`SDK_CAPABILITY_SPEC.md` §16](../../SDK_CAPABILITY_SPEC.md#16-licenciamento-decisão-aberta--não-bloqueia-implementação-local)).
To consume locally:

```bash
cd sdks/java
mvn install
```

```xml
<dependency>
    <groupId>com.ishtaran</groupId>
    <artifactId>ishtaran-java</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Or Gradle (`mavenLocal()`):

```groovy
dependencies {
    implementation 'com.ishtaran:ishtaran-java:1.0.0-SNAPSHOT'
}
```

Requires **Java 17+**.

## Quickstart

```java
var client = IshtaranClient.builder()
        .apiKey(System.getenv("ISHTARAN_API_KEY"))
        .environment(Environment.LOCAL) // or SANDBOX/PRODUCTION with an explicit baseUrl — see CONFIGURATION.md
        .build();

var balance = client.getBalance(accountId, assetNetworkId);
System.out.println("Available: " + balance.available());
```

See [`GETTING_STARTED.md`](GETTING_STARTED.md) and [`examples/`](examples/) for the complete flow.

## Documentation

| Document | Content |
|---|---|
| [GETTING_STARTED.md](GETTING_STARTED.md) | First use, step by step |
| [AUTHENTICATION.md](AUTHENTICATION.md) | `X-Api-Key` vs. Member JWT |
| [EASY_MODE.md](EASY_MODE.md) | When to use Easy Mode vs. Core |
| [CORE_API.md](CORE_API.md) | Complete resource coverage |
| [ERROR_HANDLING.md](ERROR_HANDLING.md) | `IshtaranError` hierarchy |
| [IDEMPOTENCY.md](IDEMPOTENCY.md) | Automatic vs. explicit key |
| [RETRIES.md](RETRIES.md) | Retry policy |
| [WEBHOOKS.md](WEBHOOKS.md) | Signature verification |
| [CONFIGURATION.md](CONFIGURATION.md) | `IshtaranClientConfig` |
| [SECURITY.md](SECURITY.md) | Secrets, TLS, redaction |
| [FEATURES.md](FEATURES.md) | Capability coverage (derived from `SDK_FEATURE_MATRIX.md`) |
| [CHANGELOG.md](CHANGELOG.md) | Version history |

## Grounding

Every behavior of this SDK is extracted from the real API (`website/openapi/ishtaran-api.json`)
and from the backend's source code (authentication, errors, webhooks, rate limiting) — never
invented. See [`SDK_CAPABILITY_SPEC.md`](../../SDK_CAPABILITY_SPEC.md) for the complete contract,
including every real API gap explicitly documented (no request ID, asymmetric enum format per
module, money as `number` in JSON, etc.).
