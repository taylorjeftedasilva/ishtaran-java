# Security

Ver `SECURITY_REVIEW.md` para o checklist formal completo. Resumo do comportamento real:

## Segredos nunca vazam

- `apiKey`, `endpointSecret` de webhook, tokens JWT: nunca aparecem em log, exceção, `toString()`
  de qualquer classe do SDK, ou serialização.
- `IshtaranClientConfig.toString()` mascara a API Key (`Redactor.mask` — 4 primeiros + `****` + 4
  últimos caracteres; nunca assume um prefixo tipo `sk_live_` que não existe de verdade na API real).
- Logging opt-in (`enableLogging(true)`) nunca loga `Authorization`/`X-Api-Key` em texto puro, nem o
  corpo bruto da requisição/resposta (que pode conter um secret recém-gerado).

## TLS

Verificação de certificado ligada por padrão, sem switch fácil de desligar em produção — a única
exceção (`allowInsecureTlsForLocalDevelopment`) exige `Environment.LOCAL` explicitamente e lança
exceção se combinada com `SANDBOX`/`PRODUCTION`.

## Webhook

`WebhookSignatureVerifier` usa `MessageDigest.isEqual` (tempo constante real da JDK), valida
timestamp contra replay, nunca loga o secret usado no cálculo.

## Dependências

Mínimas e maduras: `java.net.http.HttpClient` (JDK, zero dependência externa de transporte),
Jackson (`jackson-databind`+`jackson-datatype-jsr310`), SLF4J (fachada, sem implementação
obrigatória). Nenhuma outra dependência de terceiros.

## Reportando uma vulnerabilidade

Este SDK ainda não tem um canal de disclosure formal publicado — trate como parte do processo de
segurança do repositório principal (`docs/specs/security-adversarial-review/`) até um canal
dedicado existir.
