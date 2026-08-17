# Ishtaran Java SDK

SDK oficial em Java para a [API Ishtaran](https://ishtaran.com) — plataforma financeira
programável (contas virtuais, fluxos condicionais de liberação, liquidações e saques on-chain).

**Status:** implementação de referência do [Ishtaran Official SDK Program](../../SDK_CAPABILITY_SPEC.md)
(Java → TypeScript → Python → Go). Ver [`JAVA_SDK_CHECKPOINT.md`](../../JAVA_SDK_CHECKPOINT.md) na
raiz do repositório para o estado exato de conclusão.

## Duas camadas, mesmo backend

- **Easy Mode** — `client.receivePayment(...)`, `client.withdraw(...)`, `client.getBalance(...)`,
  `client.verifyWebhookSignature(...)`: composição rápida para integração comum, sem precisar
  entender toda a superfície da API. Nunca duplica lógica de negócio — só combina chamadas Core.
- **Core API** — `client.accounts()`, `client.transactions()`, `client.withdrawals()`, etc.: acesso
  granular a exatamente os mesmos 83 endpoints reais da API (ver
  [`SDK_FEATURE_MATRIX.md`](../../SDK_FEATURE_MATRIX.md)), sem nada inventado além do que a API
  real expõe.

## Instalação

Ainda não publicado no Maven Central (decisão de licenciamento pendente — ver
[`SDK_CAPABILITY_SPEC.md` §16](../../SDK_CAPABILITY_SPEC.md#16-licenciamento-decisão-aberta--não-bloqueia-implementação-local)).
Para consumir localmente:

```bash
cd sdks/java
mvn install
```

```xml
<dependency>
    <groupId>com.ishtaran</groupId>
    <artifactId>ishtaran-java</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Ou Gradle (`mavenLocal()`):

```groovy
dependencies {
    implementation 'com.ishtaran:ishtaran-java:1.0.0-SNAPSHOT'
}
```

Requer **Java 17+**.

## Quickstart

```java
var client = IshtaranClient.builder()
        .apiKey(System.getenv("ISHTARAN_API_KEY"))
        .environment(Environment.LOCAL) // ou SANDBOX/PRODUCTION com baseUrl explícito — ver CONFIGURATION.md
        .build();

var balance = client.getBalance(accountId, assetNetworkId);
System.out.println("Available: " + balance.available());
```

Veja [`GETTING_STARTED.md`](GETTING_STARTED.md) e [`examples/`](examples/) para o fluxo completo.

## Documentação

| Documento | Conteúdo |
|---|---|
| [GETTING_STARTED.md](GETTING_STARTED.md) | Primeiro uso, passo a passo |
| [AUTHENTICATION.md](AUTHENTICATION.md) | `X-Api-Key` vs. Member JWT |
| [EASY_MODE.md](EASY_MODE.md) | Quando usar Easy Mode vs. Core |
| [CORE_API.md](CORE_API.md) | Cobertura completa de recursos |
| [ERROR_HANDLING.md](ERROR_HANDLING.md) | Hierarquia `IshtaranError` |
| [IDEMPOTENCY.md](IDEMPOTENCY.md) | Chave automática vs. explícita |
| [RETRIES.md](RETRIES.md) | Política de retry |
| [WEBHOOKS.md](WEBHOOKS.md) | Verificação de assinatura |
| [CONFIGURATION.md](CONFIGURATION.md) | `IshtaranClientConfig` |
| [SECURITY.md](SECURITY.md) | Segredos, TLS, redação |
| [FEATURES.md](FEATURES.md) | Cobertura de capacidades (derivado de `SDK_FEATURE_MATRIX.md`) |
| [CHANGELOG.md](CHANGELOG.md) | Histórico de versões |

## Fundamentação

Todo comportamento deste SDK é extraído da API real (`website/openapi/ishtaran-api.json`) e do
código-fonte do backend (autenticação, erros, webhooks, rate limiting) — nunca inventado. Ver
[`SDK_CAPABILITY_SPEC.md`](../../SDK_CAPABILITY_SPEC.md) para o contrato completo, incluindo toda
lacuna real da API documentada explicitamente (sem request ID, formato de enum assimétrico por
módulo, dinheiro como `number` no JSON, etc.).
