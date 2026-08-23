# Configuration

`IshtaranClientConfig` (built via `IshtaranClient.builder()`) is the single source of
configuration — never scattered across individual resources, no business method accepts a URL
override.

```java
var client = IshtaranClient.builder()
        .apiKey("...")
        .environment(Environment.LOCAL)
        .baseUrl("http://localhost:8080")       // always explicit when present, never inferred
        .connectTimeout(Duration.ofSeconds(5))  // default: 5s
        .requestTimeout(Duration.ofSeconds(30)) // default: 30s
        .enableLogging(true)                    // opt-in, truly opt-in — never on by default
        .build();
```

## `baseUrl`/`Environment`

| Environment | Default `baseUrl` | Needs explicit `.baseUrl(...)`? |
|---|---|---|
| `LOCAL` | `http://localhost:8080` | No |
| `SANDBOX` | **none** — real infrastructure not yet provisioned | **Yes, required** |
| `PRODUCTION` | **none** — same | **Yes, required** |

Building the client with `SANDBOX`/`PRODUCTION` without an explicit `baseUrl` throws
`IllegalStateException` immediately (fail-fast) — the SDK never silently points to a made-up URL.
Once the real `Endpoints.SANDBOX`/`PRODUCTION` exist (after a real `terraform apply`), this
requirement goes away without breaking backward compatibility.

## TLS

TLS is required by default, with no easy switch to disable it. The only exception
(`allowInsecureTlsForLocalDevelopment`) only takes effect with `Environment.LOCAL` — combining it
with `SANDBOX`/`PRODUCTION` throws in `build()`.

## User-Agent

Fixed format `ishtaran-java/<version>` — never contains personal data, not configurable.
