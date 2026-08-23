# Ishtaran Java SDK

Official Java SDK for the [Ishtaran API](https://ishtaran.com) — a programmable financial
platform (virtual accounts, conditional release workflows, settlements, and self-custody
blockchain execution).

**Development Preview · Public Sandbox coming soon · Production not yet available**

## Project status

Ishtaran is currently under active development.

The official SDKs already include the current HTTP API capabilities and the self-custody
wallet/signing protocol, but the public Sandbox is still being prepared.

- **Public Sandbox target:** approximately two weeks from August 23, 2026. This is a planned,
  expected target, not a guarantee.
- **Production blockchain execution is not available yet.**

See [Sandbox](#sandbox) and [Production status](#production-status) below for what that means
concretely today.

## What this SDK does

Reference implementation of the Ishtaran Official SDK Program
(Java → TypeScript → Python → Go), 100% functional parity across all four. See also:
[TypeScript/Node.js](https://github.com/taylorjeftedasilva/ishtaran-node) ·
[Python](https://github.com/taylorjeftedasilva/ishtaran-python) ·
[Go](https://github.com/taylorjeftedasilva/ishtaran-go).

Two layers over the same backend:

- **Easy Mode** — `client.receivePayment(...)`, `client.withdraw(...)`, `client.getBalance(...)`,
  `client.verifyWebhookSignature(...)`: fast composition for common integrations, without needing
  to understand the whole API surface. Never duplicates business logic — only combines Core
  calls.
- **Core API** — `client.accounts()`, `client.transactions()`, `client.withdrawals()`, etc.:
  granular access to exactly the same real API endpoints, with nothing invented beyond what the
  real API exposes.
- **AccountHolders** — `client.accountHolders()`: self-service for the financial holder's global
  identity — `signUp`/`login`/`me`/`claimInvitation`/`signUpAndClaimInvitation`.
  Isolated session: never shares a token with `client.auth()` (Member) nor with the
  Organization's API Key within the same client instance.

## Self-custody

**Your keys stay with you. The SDK signs locally. Ishtaran verifies and relays. The blockchain
executes.**

- Wallet generation/restoration happens client-side, inside this SDK.
- Private keys, seeds, and mnemonic phrases never need to be sent to Ishtaran.
- Signing happens in your own environment/process.
- The SDK validates the signing context before signing.
- Ishtaran only ever receives public wallet/derivation material and signed execution payloads.
- Ishtaran verifies each signature, relays the transaction, and monitors and reconciles
  execution.
- Sandbox and Production use the same signing semantics from the SDK's perspective — environment
  behavior (simulated vs. real execution) is resolved by the Ishtaran API/infrastructure, never
  by a special cryptographic code path inside the SDK.

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

See [`examples/Example13SelfCustodySigning.java`](examples/src/main/java/com/ishtaran/examples/Example13SelfCustodySigning.java)
for the full runnable flow, and
[Self-Custody](https://ishtaran.com/docs/concepts/self-custody) for the complete protocol detail.

## Current capabilities

- Organizations / Applications / Environments
- API Keys
- Accounts / AccountHolders
- Payment Intents / Deposits
- Ledger
- Transactions
- Workflows / Rules
- Settlements / Splits / Fees / Refunds
- Withdrawals
- Webhooks
- Self-custody: wallet generation/restore, public address derivation, `SigningRequest`
  validation, local signing, signed transaction submission

This is deliberately not a full reference — see [FEATURES.md](FEATURES.md) and the
[API Reference](https://ishtaran.com/docs/api/ishtaran-api) for details.

## Installation

Not yet published on Maven Central — package registry distribution is planned alongside the
public Sandbox launch (see [Package distribution roadmap](#package-distribution-roadmap)). The
source is public today; clone and install into your local Maven repository:

```bash
git clone https://github.com/taylorjeftedasilva/ishtaran-java.git
cd ishtaran-java
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

## Quick example

```java
var client = IshtaranClient.builder()
        .apiKey(System.getenv("ISHTARAN_API_KEY"))
        .environment(Environment.LOCAL) // or SANDBOX/PRODUCTION with an explicit baseUrl — see CONFIGURATION.md
        .build();

var balance = client.getBalance(accountId, assetNetworkId);
System.out.println("Available: " + balance.available());
```

See [`GETTING_STARTED.md`](GETTING_STARTED.md) and [`examples/`](examples/) for the complete flow.

## Sandbox

The public Sandbox is not live yet.

- **Planned target:** approximately two weeks from August 23, 2026.
- Sandbox uses simulated blockchain execution — no real funds are involved.
- The self-custody signing protocol described above is still fully exercised in Sandbox:
  signatures are not skipped just because execution is simulated.

This section will be updated with real onboarding steps when the public Sandbox launches.

## Production status

**Production blockchain execution is not available yet.**

Additional networks/assets may be mentioned elsewhere in this project as roadmap items — none of
them should be read as available in Production today.

## Security

- Never commit API keys.
- Never transmit mnemonic phrases, seeds, or private keys to Ishtaran — there is no legitimate
  reason for any Ishtaran API call to ever need them.
- Use a production-grade KeyStore/Signer implementation for real deployments.
- The reference `InMemorySigner` shipped in this SDK is an example, not a production
  secret-storage solution.
- Verify the expected destination, asset, amount, and signing context before signing.
- Treat any integration, tool, or request asking you to upload private key material as invalid.

See [SECURITY.md](SECURITY.md) for more detail.

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
| [FEATURES.md](FEATURES.md) | Capability coverage |
| [CHANGELOG.md](CHANGELOG.md) | Version history |

Every behavior of this SDK is extracted from the real API and from the backend's source code
(authentication, errors, webhooks, rate limiting) — never invented. See the
[Documentation](https://ishtaran.com/docs/intro) and
[API Reference](https://ishtaran.com/docs/api/ishtaran-api) / [raw OpenAPI](https://ishtaran.com/openapi.json)
for the complete real contract.

## Package distribution roadmap

Not yet published on a package registry. Alongside the public Sandbox launch, the plan is to
distribute this SDK through Maven Central (or another standard Maven-compatible repository).
Until then, build/install directly from this source repository — see
[Installation](#installation) above.

## License

Not yet decided by the project — see [`pom.xml`](pom.xml). No license terms should be assumed
until a formal decision is published here.
