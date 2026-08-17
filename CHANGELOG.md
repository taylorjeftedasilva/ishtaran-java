# Changelog

Segue [SemVer](https://semver.org/). Ainda não publicado (Maven Central) — versões abaixo refletem
o estado do desenvolvimento local.

## [1.0.0-SNAPSHOT] — 2026-08-17

Primeira implementação — Java é a linguagem de referência do Ishtaran Official SDK Program.

### Adicionado

- Client central (`IshtaranClient`, builder, `IshtaranClientConfig`).
- Core API completo — 16 módulos, 93 operações reais (Organizations, Applications, Environments,
  ApiKeys, Members, AssetNetworkCatalog, Accounts, Transactions, Deposits, Ledger, Settlements,
  Refunds, Withdrawals, WorkflowRules/EventTypes/Events, Sandbox, WebhookEndpoints/Deliveries).
- Easy Mode — `receivePayment`/`getPayment`/`waitForPayment`, `withdraw`, `getBalance`,
  `verifyWebhookSignature`.
- Autenticação `X-Api-Key` + Member JWT (`auth().login()`).
- Hierarquia `IshtaranError` completa (10 subtipos) com mapeamento fiel ao `ProblemDetails` real.
- Retry seguro com backoff+jitter, respeitando `Retry-After`.
- Idempotência (campo de corpo E header, conforme o endpoint real) com auto-geração.
- Paginação real (`PageIterator`, 2 endpoints) + listas simples para os demais.
- Enums com fallback `UNKNOWN` preservando o valor bruto (forward-compatibility real).
- `WebhookSignatureVerifier` (HMAC-SHA256, tempo constante, tolerância de replay).
- Logging opt-in com redação central de secrets.
- Empacotamento Maven validado via dry run real (consumo por projeto Maven e Gradle de amostra).

### Conhecido, ainda pendente

- Documentação de exemplos completos (`examples/`) — ver `SECURITY_REVIEW.md` e
  `JAVA_SDK_CHECKPOINT.md` para o estado exato.
- Publicação real no Maven Central — bloqueada por decisão de licenciamento pendente.
