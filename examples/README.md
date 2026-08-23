# Ishtaran Java SDK — Examples

13 numbered examples, real code (never pseudocode), compiled and verified in this Maven module
against the real SDK (`com.ishtaran:ishtaran-java`).

| # | File | Demonstrates |
|---|---|---|
| 01 | `Example01Auth.java` | Minimal quickstart — API key → client → first call |
| 02 | `Example02CreateAccount.java` | Create an Account (Core) |
| 03 | `Example03ReceivePaymentEasy.java` | Receive a payment (Easy Mode) + `waitForPayment` |
| 04 | `Example04CreateTransactionCore.java` | Create a Transaction with participants (Core) |
| 05 | `Example05PaymentIntentCore.java` | Payment Intent + real `depositAddress` (Core) |
| 06 | `Example06Settlement.java` | Settle a Transaction + summary (Core) |
| 07 | `Example07WithdrawalQuote.java` | Quote a withdrawal, Network Fee always visible (Core) |
| 08 | `Example08Withdrawal.java` | Execute a withdrawal (Easy Mode) + `waitFor` |
| 09 | `Example09Ledger.java` | Balance + Ledger Entries with real pagination (Core) |
| 10 | `Example10WebhookVerification.java` | Signature verification — **the only one 100% runnable without a real API** |
| 11 | `Example11Sandbox.java` | Faucet + simulated confirmation (Sandbox) |
| 12 | `Example12AccountHolderInvitation.java` | AccountHolder invitation + signup-and-claim (DEC-032) |
| 13 | `Example13SelfCustodySigning.java` | End-to-end self-custody: generates a local wallet, registers it, allocates an address, creates/signs/submits a `SigningRequest`, confirms broadcast (SPEC-017-021) |

## Running

All of them require a real Ishtaran API instance running, except `10`:

```bash
export ISHTARAN_API_KEY=...
export ISHTARAN_ORGANIZATION_ID=...
# ... remaining variables per example, see the top of each file

mvn compile exec:java -Dexec.mainClass=com.ishtaran.examples.Example01Auth
```

`Example10WebhookVerification` runs without any real environment variables (local computation,
no HTTP call):

```bash
mvn compile exec:java -Dexec.mainClass=com.ishtaran.examples.Example10WebhookVerification
```

## Prerequisites not covered here

Creating the Organization/first Member/Asset Network is out of scope for these examples, for the
same reason documented in `examples/quickstart-node/README.md` (repository root) — none of these
actions is something an integrator performs on their own via the public API today.
