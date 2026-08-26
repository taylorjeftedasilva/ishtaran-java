# Authentication

Two real mechanisms — never one simulated by the other (see `SDK_CAPABILITY_SPEC.md` §3).

## `X-Api-Key` (recommended — machine-to-machine)

```java
var client = IshtaranClient.builder()
        .apiKey("<your Application/Environment API Key>")
        .environment(Environment.LOCAL)
        .build();
```

- Real header: `X-Api-Key` (never `Authorization: Bearer`).
- Confirms the identity of an entire `Organization`+`Application`+`Environment` — no granular RBAC.
- Works for read **and write** on the 8 Data Plane modules: `Accounts`, `Transactions`,
  `Deposits`, `Ledger`, `Settlements`/`Refunds`, `Withdrawals`, `WorkflowRules`, `Sandbox`.
- **Does not work today** for: `Organizations`/`Applications`/`Environments`/`Members`/`ApiKeys`
  (Control Plane), reading `AssetNetworkCatalog`, managing `WebhookEndpoint` — Member JWT only
  (real API gaps, see `SDK_CAPABILITY_SPEC.md` §12.3/§12.4).
- No environment prefix (`sk_live_`/`sk_test_` don't really exist) — Sandbox/Production
  isolation comes entirely from separate physical infrastructure, not from the key's format.

## Member JWT (human login — required for Control Plane)

```java
var token = client.auth().login(email, password);
// The client now uses the token internally for every subsequent Control Plane call.

var organizations = client.organizations().get(organizationId);
```

`client.auth().login(...)` stores the `accessToken` internally — subsequent calls reuse it
automatically via `Authorization: Bearer`, with no manual passing required.

## Never mix the two

The SDK never sends the API Key as a Bearer token, nor the JWT as `X-Api-Key` — each credential
always goes in its corresponding real header. If both are configured (API Key in
`IshtaranClientConfig` + login performed), both headers are sent on Data Plane routes
(dual-scheme); which one the backend uses when both represent different identities has not been
verified live by this SDK — avoid configuring both simultaneously against different
Organizations.