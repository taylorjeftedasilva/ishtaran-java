# Easy Mode vs. Core API

Same backend, two layers — Easy Mode never duplicates business logic, it only composes Core
calls (see `SDK_CAPABILITY_SPEC.md` §5).

## Use Easy Mode when...

- You want to integrate quickly without understanding the whole API surface
  (`client.receivePayment(...)`, `client.withdraw(...)`, `client.getBalance(...)`).
- You need to safely wait for an asynchronous result (`client.waitForPayment(...)`,
  `client.withdrawals().waitFor(...)`, `client.transactions().waitFor(...)` — always with a
  timeout).
- You only need to verify a webhook signature (`client.verifyWebhookSignature(...)`).

## Use Core API when...

- You need granular control (e.g. reserving balance separately from settling —
  `client.transactions().reserve(...)` vs. `client.settlements().executeSettlement(...)`).
- You need a resource Easy Mode doesn't cover (`client.workflows()`, `client.sandbox()`,
  `client.organizations()`, etc. — 93 real operations, see `SDK_FEATURE_MATRIX.md`).
- You want real pagination (`client.withdrawals().listAll(...)`,
  `client.ledger().listAllEntries(...)`) instead of a single call.
- You're building an admin panel/dashboard that needs every field of a response, not just the
  summary Easy Mode exposes.

## Concrete equivalence

| Easy Mode | Core equivalent |
|---|---|
| `client.receivePayment(orgId, appId, payer, recipient, assetNetworkId, amount)` | `client.transactions().create(...)` + `client.deposits().createPaymentIntent(...)` + `client.deposits().getPaymentIntent(...)` |
| `client.withdraw(orgId, accountId, assetNetworkId, amount, address, null)` | `client.withdrawals().createDestination(...)` + `client.withdrawals().request(...)` |
| `client.getBalance(accountId, assetNetworkId)` | `client.ledger().getBalance(accountId, assetNetworkId)` |
| `client.waitForPayment(...)` | manual polling of `client.transactions().get(...)` + `client.deposits().getPaymentIntent(...)` |

Easy Mode never hides the real `withdrawalId`/`transactionId`/`paymentIntentId` — every Easy Mode
result exposes the real Core IDs for debugging (per the brief's rule).

## Network Fee is never hidden

`client.withdraw(...)` always returns `estimatedNetworkFee`/`estimatedRecipientAmount`/`status`
along with `requestedAmount` — never just "success"/"failure". If you need to know the fee BEFORE
committing to a withdrawal, use `client.withdrawals().quote(...)` (Core, pure read, never reserves
balance).
