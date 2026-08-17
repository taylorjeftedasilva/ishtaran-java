# SECURITY_REVIEW.md — Ishtaran Java SDK

Checklist do §57 do brief do SDK Program. Cada item marcado com evidência real (teste ou leitura de
código), nunca assumido. **PASS** com 2 limitações conhecidas documentadas explicitamente (nunca
escondidas) — ver seção final.

| # | Item | Status | Evidência |
|---|---|---|---|
| 1 | Secrets nunca logados | ✅ PASS | `LoggingTransportTest.redactedHeaders_neverExposesApiKeyOrAuthorizationInPlainText`; `RedactorTest` |
| 2 | API Key nunca na URL/querystring | ✅ PASS | `AuthenticatingTransport` só anexa via header (`X-Api-Key`/`Authorization`); nenhum resource constrói URL com a chave — confirmado por leitura de todo `resources/*.java` |
| 3 | Verificação de TLS ligada por padrão | ✅ PASS | `JdkHttpTransport` nunca desliga verificação; `allowInsecureTlsForLocalDevelopment` só existe combinado com `Environment.LOCAL`, lança em `build()` caso contrário — `IshtaranClientConfigTest.insecureTlsOverride_onlyAllowedForLocal` |
| 4 | Comparação de assinatura de webhook em tempo constante | ✅ PASS | `MessageDigest.isEqual` real (não `String.equals`) — `WebhookSignatureVerifierTest` (7 testes, incluindo payload/assinatura adulterados e vetor calculado independentemente em Python) |
| 5 | Retries seguros (nunca cegos em mutação não-idempotente) | ✅ PASS | `RetryingTransportTest.status503_nonIdempotentRequest_neverRetried`; nunca retry em 400/401/403/404/409/422 |
| 6 | Timeout obrigatório, nunca infinito por padrão | ✅ PASS | `IshtaranClientConfigTest.defaults_areSaneAndFinite_neverInfiniteTimeout` — connect 5s/request 30s |
| 7 | Redação central em logging opt-in | ✅ PASS | `LoggingTransport` nunca loga corpo bruto, só método/path/status/duração; headers sensíveis mascarados |
| 8 | Dependências mínimas, escaneadas | ⚠️ PARCIAL | 3 dependências de produção (`jackson-databind`, `jackson-datatype-jsr310`, `slf4j-api`), todas maduras/amplamente usadas. Scan automatizado de CVE (`dependency-check-maven`) **não executado nesta sessão** — preparado em `JAVA_SDK_IMPLEMENTATION_PLAN.md` §13, fica para o pipeline de CI real |
| 9 | Dinheiro nunca perde precisão | ✅ PASS | `MoneyPrecisionTest` — `BigDecimal` lido direto do token JSON, nunca via `double` intermediário |
| 10 | Resposta maliciosa/malformada nunca derruba o client | ✅ PASS | `ErrorMapperTest.malformedOrEmptyBody_neverThrowsParsingException_fallsBackToApiError`; enums desconhecidos nunca lançam (`EnumForwardCompatibilityTest`) |
| 11 | Corpo de resposta com tamanho ilimitado | ⚠️ **LIMITAÇÃO REAL, NÃO CORRIGIDA** | `JdkHttpTransport` usa `BodyHandlers.ofString()`, que buferiza a resposta inteira em memória sem limite de tamanho. Um servidor comprometido/MITM que devolva um corpo arbitrariamente grande pode causar exaustão de memória. Ver "Limitações conhecidas" abaixo |
| 12 | Desserialização segura (sem gadget/polymorphic deserialization) | ✅ PASS | `JsonCodec` nunca habilita `enableDefaultTyping`/polymorphic type resolution do Jackson — só desserializa para tipos concretos conhecidos (records), nunca para `Object`/tipo aberto a partir de um campo do payload |
| 13 | URL controlada pelo usuário / risco de SSRF | ✅ PASS | `baseUrl` é sempre explícito e fixado na construção do client — nenhum método de negócio individual aceita override de URL (verificado: nenhum método em `resources/*.java` recebe parâmetro de URL/endpoint) |
| 14 | Comportamento de redirecionamento HTTP | ✅ PASS (default seguro da JDK) | `HttpClient.newBuilder()` sem `.followRedirects(...)` explícito usa o default `Redirect.NEVER` da JDK — o SDK nunca segue redirect automaticamente, evitando um redirect malicioso desviar a chamada para outro host |
| 15 | Injeção de header | ✅ PASS | Nomes/valores de header passam pela validação nativa de `java.net.http.HttpRequest.Builder.header()` (RFC 7230), que rejeita CR/LF e caracteres inválidos — nunca construído por concatenação de string crua |
| 16 | Comportamento de proxy | N/A | Não aplicável — nenhuma configuração de proxy customizada é exposta nesta versão; `HttpClient` usa o proxy do sistema, comportamento padrão da JDK |

## Achado corrigido durante esta revisão

**Injeção de query string em `webhookEndpoints().listDeliveries(eventType, ...)`** — o valor de
`eventType` (string livre fornecida pelo consumidor) era concatenado diretamente na query string
sem URL-encoding, permitindo que um valor malicioso (ex. `"x&status=DELIVERED"`) injetasse um
segundo parâmetro de query não intencional. Corrigido com `URLEncoder.encode(...)`, coberto por
`WebhookEndpointsResourceTest.listDeliveries_eventTypeFilter_isUrlEncoded_neverInjectsExtraQueryParams`.
Todo outro valor interpolado em query string do SDK é ou um `UUID`/enum de conjunto fechado (sem
risco de injeção) ou já formatado por `OffsetDateTime.toString()` (ISO-8601, charset seguro).

## Limitações conhecidas (documentadas, não escondidas)

1. **Corpo de resposta sem limite de tamanho** (item 11) — aceito nesta versão porque a API real
   (Ishtaran) não é uma origem não-confiável no fluxo normal de uso do SDK; o risco só existe se o
   `baseUrl` apontar para um host comprometido. Mitigação futura: trocar `BodyHandlers.ofString()`
   por um handler com limite configurável de tamanho.
2. **Scan de dependências automatizado não executado** (item 8) — as 3 dependências de produção são
   bibliotecas maduras e amplamente escaneadas na comunidade (Jackson, SLF4J), mas nenhuma
   verificação de CVE foi rodada nesta sessão especificamente contra as versões fixadas no `pom.xml`.

Nenhuma das duas limitações bloqueia o uso do SDK contra a API Ishtaran real hoje — ambas são
registradas para tratamento antes de uma eventual publicação pública (Maven Central).

## Veredito

**PASS**, com as 2 limitações acima documentadas explicitamente (nunca escondidas) — nenhum achado
crítico ou de alta severidade permanece sem correção ou sem justificativa registrada.
