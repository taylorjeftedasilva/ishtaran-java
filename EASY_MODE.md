# Easy Mode vs. Core API

Mesmo backend, duas camadas — Easy Mode nunca duplica lógica de negócio, só compõe chamadas Core
(ver `SDK_CAPABILITY_SPEC.md` §5).

## Use Easy Mode quando...

- Você quer integrar rápido sem entender toda a superfície da API (`client.receivePayment(...)`,
  `client.withdraw(...)`, `client.getBalance(...)`).
- Você precisa esperar um resultado assíncrono de forma segura (`client.waitForPayment(...)`,
  `client.withdrawals().waitFor(...)`, `client.transactions().waitFor(...)` — sempre com timeout).
- Você só precisa verificar uma assinatura de webhook (`client.verifyWebhookSignature(...)`).

## Use Core API quando...

- Você precisa de controle granular (ex.: reservar saldo separadamente de liquidar —
  `client.transactions().reserve(...)` vs. `client.settlements().executeSettlement(...)`).
- Você precisa de um recurso que o Easy Mode não cobre (`client.workflows()`, `client.sandbox()`,
  `client.organizations()`, etc. — 93 operações reais, ver `SDK_FEATURE_MATRIX.md`).
- Você quer paginar de verdade (`client.withdrawals().listAll(...)`,
  `client.ledger().listAllEntries(...)`) em vez de uma única chamada.
- Você está construindo um painel administrativo/dashboard que precisa de todos os campos de uma
  resposta, não só o resumo que o Easy Mode expõe.

## Equivalência concreta

| Easy Mode | Core equivalente |
|---|---|
| `client.receivePayment(orgId, appId, payer, recipient, assetNetworkId, amount)` | `client.transactions().create(...)` + `client.deposits().createPaymentIntent(...)` + `client.deposits().getPaymentIntent(...)` |
| `client.withdraw(orgId, accountId, assetNetworkId, amount, address, null)` | `client.withdrawals().createDestination(...)` + `client.withdrawals().request(...)` |
| `client.getBalance(accountId, assetNetworkId)` | `client.ledger().getBalance(accountId, assetNetworkId)` |
| `client.waitForPayment(...)` | polling manual de `client.transactions().get(...)` + `client.deposits().getPaymentIntent(...)` |

Easy Mode nunca esconde o `withdrawalId`/`transactionId`/`paymentIntentId` real — todo resultado
Easy Mode expõe os IDs reais do Core para debugging (regra do brief).

## Network Fee nunca escondida

`client.withdraw(...)` sempre devolve `estimatedNetworkFee`/`estimatedRecipientAmount`/`status`
junto do `requestedAmount` — nunca só "sucesso"/"falha". Se você precisa saber a taxa ANTES de
comprometer o saque, use `client.withdrawals().quote(...)` (Core, leitura pura, nunca reserva
saldo).
