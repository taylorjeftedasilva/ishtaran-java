# Security

See `SECURITY_REVIEW.md` for the complete formal checklist. Summary of the real behavior:

## Secrets never leak

- `apiKey`, webhook `endpointSecret`, JWT tokens: never appear in logs, exceptions, any SDK
  class's `toString()`, or serialization.
- `IshtaranClientConfig.toString()` masks the API Key (`Redactor.mask` — first 4 + `****` + last
  4 characters; never assumes a `sk_live_`-style prefix that doesn't really exist in the real
  API).
- Opt-in logging (`enableLogging(true)`) never logs `Authorization`/`X-Api-Key` in plain text, nor
  the raw request/response body (which may contain a freshly generated secret).

## TLS

Certificate verification is on by default, with no easy switch to disable it in production — the
only exception (`allowInsecureTlsForLocalDevelopment`) explicitly requires `Environment.LOCAL` and
throws if combined with `SANDBOX`/`PRODUCTION`.

## Webhook

`WebhookSignatureVerifier` uses `MessageDigest.isEqual` (the JDK's real constant-time comparison),
validates the timestamp against replay, and never logs the secret used in the calculation.

## Dependencies

Minimal and mature: `java.net.http.HttpClient` (JDK, zero external transport dependency),
Jackson (`jackson-databind`+`jackson-datatype-jsr310`), SLF4J (facade, no implementation
required). No other third-party dependency.

## Reporting a vulnerability

This SDK doesn't yet have a published formal disclosure channel — treat it as part of the main
repository's security process (`docs/specs/security-adversarial-review/`) until a dedicated
channel exists.
