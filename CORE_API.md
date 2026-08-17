# Core API

Cobertura completa e literal da API real (83 rotas em escopo, 16 módulos — ver
`SDK_FEATURE_MATRIX.md` para a lista rota-a-rota e `SDK_METHOD_MAP.md` para o nome exato de cada
método). Nenhum endpoint inventado; nenhum endpoint admin-only/platform-only exposto (fora de
escopo do SDK, ver `SDK_CAPABILITY_SPEC.md` §1).

## Control Plane (sempre Member JWT)

| Resource | Método de acesso |
|---|---|
| Organizations | `client.organizations()` |
| Applications | `client.applications()` |
| Environments | `client.environments()` |
| ApiKeys | `client.apiKeys()` |
| Members | `client.members()` |
| AssetNetworkCatalog (leitura) | `client.assetNetworkCatalog()` |
| WebhookEndpoints (config) | `client.webhookEndpoints()` |
| WebhookDeliveries | `client.webhookDeliveries()` |

## Data Plane (API Key ou Member JWT)

| Resource | Método de acesso |
|---|---|
| Accounts | `client.accounts()` |
| Transactions | `client.transactions()` |
| Deposits | `client.deposits()` |
| Ledger | `client.ledger()` |
| Settlements | `client.settlements()` |
| Refunds | `client.refunds()` |
| Withdrawals | `client.withdrawals()` |
| Workflows/EventTypes/Events | `client.workflows()` / `client.eventTypes()` / `client.events()` |
| Sandbox (só Environment Sandbox) | `client.sandbox()` |

## Exemplo — fluxo completo sem Easy Mode

```java
var account = client.accounts().create(organizationId, "customer-123");
client.accounts().authorizeApplication(account.accountId(), applicationId);

var txn = client.transactions().create(organizationId, applicationId, null, assetNetworkId,
        amount, List.of(payer, recipient), null);

var intent = client.deposits().createPaymentIntent(organizationId, txn.transactionId(),
        assetNetworkId, amount, null, null);
var fullIntent = client.deposits().getPaymentIntent(intent.paymentIntentId());
// fullIntent.depositAddress() -- endereço real para observar on-chain

var settlement = client.settlements().executeSettlement(txn.transactionId(), null);
```

## Objetos anônimos reais

Vários endpoints `POST` da API real devolvem um objeto mínimo (ex.: `{ accountId }`,
`{ transactionId }`) em vez do recurso completo — confirmado linha a linha no código-fonte dos
handlers reais, nunca assumido. O SDK modela isso fielmente (`CreateAccountResult`,
`CreateTransactionResult`, etc.) em vez de fingir que a resposta completa é devolvida. Busque o
recurso completo com o `get(...)` correspondente quando precisar de todos os campos.
