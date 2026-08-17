# Authentication

Dois mecanismos reais — nunca um simulado como o outro (ver `SDK_CAPABILITY_SPEC.md` §3).

## `X-Api-Key` (recomendado — machine-to-machine)

```java
var client = IshtaranClient.builder()
        .apiKey("<sua API Key de Application/Environment>")
        .environment(Environment.LOCAL)
        .build();
```

- Header real: `X-Api-Key` (nunca `Authorization: Bearer`).
- Confere identidade de `Organization`+`Application`+`Environment` inteiros — sem RBAC granular.
- Funciona em leitura **e escrita** para os 8 módulos Data Plane: `Accounts`, `Transactions`,
  `Deposits`, `Ledger`, `Settlements`/`Refunds`, `Withdrawals`, `WorkflowRules`, `Sandbox`.
- **Não funciona hoje** para: `Organizations`/`Applications`/`Environments`/`Members`/`ApiKeys`
  (Control Plane), leitura de `AssetNetworkCatalog`, gestão de `WebhookEndpoint` — só Member JWT
  (lacunas reais da API, ver `SDK_CAPABILITY_SPEC.md` §12.3/§12.4).
- Sem prefixo de ambiente (`sk_live_`/`sk_test_` não existem de verdade) — o isolamento
  Sandbox/Production vem inteiramente de infraestrutura física separada, não do formato da chave.

## Member JWT (login humano — obrigatório para Control Plane)

```java
var token = client.auth().login(email, password);
// client agora usa o token internamente em toda chamada de Control Plane subsequente.

var organizations = client.organizations().get(organizationId);
```

`client.auth().login(...)` guarda o `accessToken` internamente — chamadas seguintes reutilizam-no
automaticamente via `Authorization: Bearer`, sem repasse manual.

## Nunca misture os dois

O SDK nunca envia a API Key como Bearer nem o JWT como `X-Api-Key` — cada credencial vai sempre no
header real correspondente. Se ambos estiverem configurados (API Key no `IshtaranClientConfig` +
login feito), os dois headers são enviados nas rotas Data Plane (dual-scheme); qual dos dois o
backend usa quando ambos representam identidades diferentes não foi verificado ao vivo por este
SDK — evite configurar os dois simultaneamente contra Organizations diferentes.
