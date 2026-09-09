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
| Accounts | `client.accounts()` — **except `authorizeApplication`/`freeze`/`unfreeze`/`close`/`revokeRelationship`, which reject an API Key and require a Member session** (verified live, not documented anywhere else — `MemberPermissionPolicy.Require`, `AccountsEndpoints.cs`) |
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
allocation, `SigningRequest` creation/submission. `client.executionDestinations()` — registers the
real on-chain address a beneficiary `Account` gets paid at for a given `AssetNetwork`; required
before a `Settlement` can execute under SelfCustody (`DEC-037`) — `settlements().executeSettlement`
fails fast, before any signing/broadcast, if a participant has none registered. Covered with a full
worked example in [README.md § Self-custody](README.md#self-custody) rather than duplicated here —
the interesting part of this module is the local signing flow, not the HTTP resource shape.

## Network Execution Engine (`ExecutionCustody`)

`client.networkExecution().quote(environmentId, assetNetworkId, operations, networkCostPayer)`
prices a plan of 1..N physical on-chain operations (`SPEC-NETEXEC-001`). It is a **preview only** —
it never writes anything, and Settlement/Withdrawal/Payout each get/re-get their own quote
internally at execution time (`preview quote != execution quote`, never reuse this response as a
price guarantee). The response's `totalCharged` (in `quoteCurrency`) is what gets debited;
`nativeExecutionCost`/`authorizedNativeCost` are always in the resource asset's native units;
`margin()` is the Ishtaran markup applied in `ISHTARAN_RESOURCES` mode (always `0` in
`CUSTOMER_RESOURCES` mode).

Two registrations gate which mode an Organization actually runs in for a given `AssetNetwork`:

- `client.executionSources().register(...)` registers the address `ExecutionCustody` signs FROM to
  pay network cost; `client.executionSources().syncResourceStake(organizationId, executionSourceId,
  availableNativeAmount, availableEnergy, availableBandwidth)` is the self-reported (no on-chain
  verification in this version) declaration of that address's available on-chain resource
  capacity — required before `CUSTOMER_RESOURCES` (`SELF`) mode can ever succeed for it, and safe
  to call again any time to re-sync (no first-registration-wins restriction, unlike `register`).
- `client.networkCostPayerAccounts().register(organizationId, assetNetworkId, accountId)`
  registers the Account debited for the *charged* cost (first-registration-wins per
  `(organizationId, assetNetworkId)`); `client.networkCostPayerAccounts().updateResourcePreference(
  organizationId, assetNetworkId, resourcePreference, allowFallbackToIshtaranResources)` switches
  that Organization's Network Execution mode for the `AssetNetwork` between `NetworkResourceSource.SELF`
  (`CUSTOMER_RESOURCES`, the integrator's own on-chain resources) and `ISHTARAN_SPONSORED` (the
  default). `allowFallbackToIshtaranResources` only matters when `resourcePreference` is `SELF` —
  it decides whether an insufficient `CUSTOMER_RESOURCES` balance falls back to
  `ISHTARAN_RESOURCES` instead of failing closed. Requires a `NetworkCostPayerAccount` already
  registered via `register` first.

## Withdrawal

- `client.withdrawals().quote(organizationId, environmentId, accountId, withdrawalDestinationId,
  assetNetworkId, amount)` — a pure read, never writes anything; the response always exposes
  `estimatedNetworkFee`/`estimatedRecipientAmount`, never hiding the network cost.
- `client.withdrawals().request(...)` — the same arguments plus an idempotency key; builds the
  real Withdrawal and, under SelfCustody, its `SigningRequest` (singular field only — Withdrawal
  never supports multi-source funding the way Settlement does, see
  [CORE_API.md § Self-custody](#self-custody-executioncustody)).
- **Destination cooldown**: a newly-registered `WithdrawalDestination` can't be withdrawn to for
  **24 hours by default** (`WithdrawalPolicy.CooldownHours`, platform-enforced floor of 1 hour —
  an Organization can raise it, never lower it below the floor). Adding a *new* destination when
  an active one already exists for the same AssetNetwork (an account-takeover pattern) uses a
  separate, longer cooldown — **7 days by default** (`DestinationChangeCooldownHours`). Neither
  cooldown has a bypass — never build a flow that assumes one.
- **Reconciliation**: a Withdrawal whose broadcast can't be automatically resolved moves to a
  terminal status the backend calls `RequiresReconciliation` (raw value `11`) rather than silently
  failing or retrying forever — surface it to a human, it needs manual platform-side resolution.
  **Known SDK gap, verified against the real backend enum**: this SDK's `WithdrawalStatus` only
  defines raw values `0`-`9` — status `11` (and `Failed`, `10`) come back as the forward-compatible
  `UNKNOWN(rawValue)` fallback rather than a named constant. Check `rawValue() == 11` if you need
  to detect this specific status today; treating any unrecognized/unknown status defensively (not
  just this one) is good practice regardless.
- `client.withdrawals().list`/`.listAll` — one of only 2 endpoints with real pagination (see
  § Real pagination below).

## Payout (`SPEC-024`/`SPEC-025`)

Payout is where Settlement's economic outcome (who owes what) turns into an actual delivery.
**Settlement != Payout**: `settlements().executeSettlement` records the economic truth
(obligations, splits, the Platform Fee) — it never itself moves a beneficiary's money on-chain.
Whether that delivery happens immediately or later depends on the Organization's `PayoutPolicy`:

- **`IMMEDIATE`** — a beneficiary's Payable is delivered the same moment as the Settlement itself;
  no `PayoutBatch` involved.
- **`MANUAL`** — the beneficiary only accrues an economic obligation until someone explicitly
  creates a `PayoutBatch` for them via `client.payout().createBatch(...)`.

These are the two `PayoutPolicy` modes with real public support today. The backend domain model
also defines `THRESHOLD` and `SCHEDULED` values, but neither has a public trigger yet (both are
rejected — no scheduler/threshold-crossing worker exists in this slice) — don't build against
them as available capabilities.

- `client.payout().getPayableSummary(accountId, assetNetworkId)` — returns a
  `PayableSummaryResponse(accrued, reservedForPayout, paid)` for that Account/AssetNetwork pair.
  **`accrued` is an economic obligation the platform owes that Account, never the same thing as
  the Account's own on-chain `available` balance** (from `accounts().getBalance`) — an Account can
  have a large `accrued` Payable and `0` available balance simultaneously (nothing paid out yet),
  or vice versa. `reservedForPayout` is currently always `0` (no batch-scoped reservation exists
  yet).
- `client.payout().createBatch(organizationId, environmentId, assetNetworkId, explicitOwnerIds,
  idempotencyKey)` — creates a `PayoutBatch` covering the given beneficiaries' currently-accrued
  obligations. This SDK slice only ever sends `trigger = MANUAL` (the only trigger the public
  route accepts). Returns a null `payoutBatchId` (204, a legitimate no-op) when none of the given
  owners had an eligible obligation.
- `client.payout().getBatch(organizationId, payoutBatchId)` — full batch state: `status`,
  per-beneficiary `obligations` (each with its own `sourceObligations`/`destinationAddress`/
  `status`), the frozen `networkExecutionQuoteSnapshot` if network execution was involved, and a
  single `signingRequestId` (a PayoutBatch's own SelfCustody signing is not multi-source the way
  Settlement's is — see [CORE_API.md § Self-custody](#self-custody-executioncustody)).

## Example — full flow without Easy Mode

```java
var account = client.accounts().create(organizationId, "customer-123");
// authorizeApplication requires the Member client (`memberClient`), never the API Key one --
// see the table above.
memberClient.accounts().authorizeApplication(organizationId, account.accountId(), applicationId);

var txn = client.transactions().create(organizationId, applicationId, null, assetNetworkId,
        amount, List.of(payer, recipient), null);

var intent = client.deposits().createPaymentIntent(organizationId, txn.transactionId(),
        assetNetworkId, amount, null, null);
var fullIntent = client.deposits().getPaymentIntent(intent.paymentIntentId());
// fullIntent.depositAddress() -- real address to watch on-chain

// Once the deposit is confirmed, the Transaction reserves itself -- no explicit reserve() call
// needed or valid in this path (verified live -- calling it here throws BR-TXN-002).
var settlement = client.settlements().executeSettlement(txn.transactionId(), null, null);
```

See [`examples/Example14MarketplaceJourney.java`](examples/src/main/java/com/ishtaran/examples/Example14MarketplaceJourney.java)
for this same flow run in full, including the Payment Intent → deposit → confirmation → self-custody
payout signing that this snippet omits.

## Real anonymous objects

Several `POST` endpoints of the real API return a minimal object (e.g. `{ accountId }`,
`{ transactionId }`) instead of the full resource — confirmed line by line in the real handlers'
source code, never assumed. The SDK models this faithfully (`CreateAccountResult`,
`CreateTransactionResult`, etc.) instead of pretending the full response is returned. Fetch the
full resource with the corresponding `get(...)` when you need every field.
