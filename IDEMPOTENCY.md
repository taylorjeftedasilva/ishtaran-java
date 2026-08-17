# Idempotency

Dois mecanismos reais e diferentes — nunca um padrão único assumido (ver `SDK_CAPABILITY_SPEC.md`
§9).

## Campo de corpo (`idempotencyKey`) — todo endpoint financeiro

`transactions().create(...)`, `deposits().createPaymentIntent(...)`,
`settlements().executeSettlement(...)`, `refunds().executeRefund(...)`,
`withdrawals().request(...)`, `events().ingest(...)`:

```java
// Omitido -- o SDK gera um UUID v4 automaticamente.
var txn = client.transactions().create(orgId, appId, null, assetNetworkId, amount, participants, null);

// Explícito -- nunca sobrescrito pelo SDK.
var txn2 = client.transactions().create(orgId, appId, null, assetNetworkId, amount, participants, "my-key-123");
```

## Header `Idempotency-Key` — só 2 endpoints reais

`organizations().create(...)` e `organizations().createApplication(...)` — confirmado em
código-fonte, os únicos 2 lugares do backend inteiro que usam `[FromHeader(Name =
"Idempotency-Key")]` em vez de campo de corpo. Mesma política de auto-geração/override explícito,
só que via header:

```java
var org = client.organizations().create("Acme Inc", null); // header gerado automaticamente
```

Nenhum outro endpoint de `OrganizationTenancy` (Environments, ApiKeys) tem qualquer proteção de
idempotência — gap real da API, não algo que o SDK deveria fingir corrigir.

## Reenvio com a mesma chave

Reenviar a mesma chave com o mesmo payload é seguro (a API trata como replay). Reenviar a mesma
chave com um payload diferente resulta em `IdempotencyConflictError` (409,
`IDEMPOTENCY_KEY_CONFLICT`) — o SDK propaga esse erro tipado, nunca o esconde.

## Retry automático nunca gera uma chave nova

Quando o `RetryingTransport` reintenta uma chamada com Idempotency-Key (ver `RETRIES.md`), ele
reenvia exatamente a mesma requisição já montada — a mesma chave da primeira tentativa, nunca uma
nova gerada por tentativa (isso quebraria a garantia de idempotência).
