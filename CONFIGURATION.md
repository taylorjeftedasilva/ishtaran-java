# Configuration

`IshtaranClientConfig` (construído via `IshtaranClient.builder()`) é a fonte única de configuração
— nunca dispersa entre resources individuais, nenhum método de negócio aceita override de URL.

```java
var client = IshtaranClient.builder()
        .apiKey("...")
        .environment(Environment.LOCAL)
        .baseUrl("http://localhost:8080")       // sempre explícito quando presente, nunca inferido
        .connectTimeout(Duration.ofSeconds(5))  // default: 5s
        .requestTimeout(Duration.ofSeconds(30)) // default: 30s
        .enableLogging(true)                    // opt-in, opt-in mesmo — nunca ligado por padrão
        .build();
```

## `baseUrl`/`Environment`

| Environment | `baseUrl` default | Precisa `.baseUrl(...)` explícito? |
|---|---|---|
| `LOCAL` | `http://localhost:8080` | Não |
| `SANDBOX` | **nenhum** — infraestrutura real ainda não provisionada | **Sim, obrigatório** |
| `PRODUCTION` | **nenhum** — idem | **Sim, obrigatório** |

Construir o client com `SANDBOX`/`PRODUCTION` sem `baseUrl` explícito lança
`IllegalStateException` imediatamente (fail-fast) — o SDK nunca aponta silenciosamente para uma URL
inventada. Quando `Endpoints.SANDBOX`/`PRODUCTION` reais existirem (após `terraform apply` real),
essa exigência deixa de existir sem quebrar retrocompatibilidade.

## TLS

TLS obrigatório por padrão, sem switch fácil de desabilitar. A única exceção
(`allowInsecureTlsForLocalDevelopment`) só tem efeito com `Environment.LOCAL` — combiná-la com
`SANDBOX`/`PRODUCTION` lança em `build()`.

## User-Agent

Formato fixo `ishtaran-java/<versão>` — nunca contém dado pessoal, não configurável.
