# Core API

Complete, literal coverage of the real API (100 routes, 16 modules — see
`SDK_FEATURE_MATRIX.md` for the route-by-route list and `SDK_METHOD_MAP.md` for each method's
exact name). No invented endpoint; no admin-only/platform-only endpoint exposed (out of scope for
the SDK, see `SDK_CAPABILITY_SPEC.md` §1).

## Control Plane (always Member JWT)

| Resource | Access method |
|---|---|
| Organizations | `client.organizations()` |
| Applications | `client.applications()` |
| Environments | `client.environments()` |
| ApiKeys | `client.apiKeys()` |
| Members | `client.members()` |
| AssetNetworkCatalog (read) | `client.assetNetworkCatalog()` |
| WebhookEndpoints (config) | `client.webhookEndpoints()` |
| WebhookDeliveries | `client.webhookDeliveries()` |

## Data Plane (API Key or Member JWT)

| Resource | Access method |
|---|---|
| Accounts | `client.accounts()` |
| Transactions | `client.transactions()` |
| Deposits | `client.deposits()` |
| Ledger | `client.ledger()` |
| Settlements | `client.settlements()` |
| Refunds | `client.refunds()` |
| Withdrawals | `client.withdrawals()` |
| Workflows/EventTypes/Events | `client.workflows()` / `client.eventTypes()` / `client.events()` |
| Sandbox (Sandbox Environment only) | `client.sandbox()` |

## AccountHolders (isolated session, own auth)

`client.accountHolders()` — the financial holder's global identity (`DEC-032`): `signUp`/`login`/
`me`/`claimInvitation`/`signUpAndClaimInvitation`. Its session token is never shared with
`client.auth()` (Member) nor with the Organization's `X-Api-Key` on the same client instance —
treat it as a third, independent authentication context. See [README.md § Self-custody /
AccountHolders](README.md#what-this-sdk-does) for the identity model.

## Self-custody (`ExecutionCustody`)

`client.wallets()` / `client.signingRequests()` — wallet registration, deposit address
allocation, `SigningRequest` creation/submission. Covered with a full worked example in
[README.md § Self-custody](README.md#self-custody) rather than duplicated here — the interesting
part of this module is the local signing flow, not the HTTP resource shape.

## Example — full flow without Easy Mode

```java
var account = client.accounts().create(organizationId, "customer-123");
client.accounts().authorizeApplication(account.accountId(), applicationId);

var txn = client.transactions().create(organizationId, applicationId, null, assetNetworkId,
        amount, List.of(payer, recipient), null);

var intent = client.deposits().createPaymentIntent(organizationId, txn.transactionId(),
        assetNetworkId, amount, null, null);
var fullIntent = client.deposits().getPaymentIntent(intent.paymentIntentId());
// fullIntent.depositAddress() -- real address to watch on-chain

var settlement = client.settlements().executeSettlement(txn.transactionId(), null);
```

## Real anonymous objects

Several `POST` endpoints of the real API return a minimal object (e.g. `{ accountId }`,
`{ transactionId }`) instead of the full resource — confirmed line by line in the real handlers'
source code, never assumed. The SDK models this faithfully (`CreateAccountResult`,
`CreateTransactionResult`, etc.) instead of pretending the full response is returned. Fetch the
full resource with the corresponding `get(...)` when you need every field.
