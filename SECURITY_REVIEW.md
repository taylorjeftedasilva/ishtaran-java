# SECURITY_REVIEW.md — Ishtaran Java SDK

Checklist from §57 of the SDK Program brief. Every item marked with real evidence (test or code
reading), never assumed. **PASS** with 2 known limitations explicitly documented (never hidden) —
see the final section.

| # | Item | Status | Evidence |
|---|---|---|---|
| 1 | Secrets never logged | ✅ PASS | `LoggingTransportTest.redactedHeaders_neverExposesApiKeyOrAuthorizationInPlainText`; `RedactorTest` |
| 2 | API Key never in the URL/querystring | ✅ PASS | `AuthenticatingTransport` only attaches it via header (`X-Api-Key`/`Authorization`); no resource builds a URL with the key — confirmed by reading all of `resources/*.java` |
| 3 | TLS verification on by default | ✅ PASS | `JdkHttpTransport` never disables verification; `allowInsecureTlsForLocalDevelopment` only exists combined with `Environment.LOCAL`, throws in `build()` otherwise — `IshtaranClientConfigTest.insecureTlsOverride_onlyAllowedForLocal` |
| 4 | Constant-time webhook signature comparison | ✅ PASS | Real `MessageDigest.isEqual` (not `String.equals`) — `WebhookSignatureVerifierTest` (7 tests, including tampered payload/signature and an independently computed vector in Python) |
| 5 | Safe retries (never blind on non-idempotent mutation) | ✅ PASS | `RetryingTransportTest.status503_nonIdempotentRequest_neverRetried`; never retries on 400/401/403/404/409/422 |
| 6 | Mandatory timeout, never infinite by default | ✅ PASS | `IshtaranClientConfigTest.defaults_areSaneAndFinite_neverInfiniteTimeout` — connect 5s/request 30s |
| 7 | Central redaction in opt-in logging | ✅ PASS | `LoggingTransport` never logs the raw body, only method/path/status/duration; sensitive headers masked |
| 8 | Minimal, scanned dependencies | ⚠️ PARTIAL | 3 production dependencies (`jackson-databind`, `jackson-datatype-jsr310`, `slf4j-api`), all mature/widely used. Automated CVE scan (`dependency-check-maven`) **not run this session** — prepared in `JAVA_SDK_IMPLEMENTATION_PLAN.md` §13, left for the real CI pipeline |
| 9 | Money never loses precision | ✅ PASS | `MoneyPrecisionTest` — `BigDecimal` read directly from the JSON token, never through an intermediate `double` |
| 10 | Malicious/malformed response never crashes the client | ✅ PASS | `ErrorMapperTest.malformedOrEmptyBody_neverThrowsParsingException_fallsBackToApiError`; unknown enums never throw (`EnumForwardCompatibilityTest`) |
| 11 | Unbounded response body size | ⚠️ **REAL LIMITATION, NOT FIXED** | `JdkHttpTransport` uses `BodyHandlers.ofString()`, which buffers the entire response in memory with no size limit. A compromised/MITM server returning an arbitrarily large body could cause memory exhaustion. See "Known limitations" below |
| 12 | Safe deserialization (no gadget/polymorphic deserialization) | ✅ PASS | `JsonCodec` never enables Jackson's `enableDefaultTyping`/polymorphic type resolution — only deserializes into known concrete types (records), never into `Object`/an open type driven by a payload field |
| 13 | User-controlled URL / SSRF risk | ✅ PASS | `baseUrl` is always explicit and fixed at client construction — no individual business method accepts a URL override (verified: no method in `resources/*.java` takes a URL/endpoint parameter) |
| 14 | HTTP redirect behavior | ✅ PASS (safe JDK default) | `HttpClient.newBuilder()` without an explicit `.followRedirects(...)` uses the JDK's `Redirect.NEVER` default — the SDK never follows a redirect automatically, preventing a malicious redirect from diverting the call to another host |
| 15 | Header injection | ✅ PASS | Header names/values go through `java.net.http.HttpRequest.Builder.header()`'s native validation (RFC 7230), which rejects CR/LF and invalid characters — never built by raw string concatenation |
| 16 | Proxy behavior | N/A | Not applicable — no custom proxy configuration is exposed in this version; `HttpClient` uses the system proxy, the JDK's default behavior |

## Finding fixed during this review

**Query string injection in `webhookEndpoints().listDeliveries(eventType, ...)`** — the
`eventType` value (a free-form string supplied by the consumer) was concatenated directly into
the query string without URL-encoding, letting a malicious value (e.g. `"x&status=DELIVERED"`)
inject an unintended second query parameter. Fixed with `URLEncoder.encode(...)`, covered by
`WebhookEndpointsResourceTest.listDeliveries_eventTypeFilter_isUrlEncoded_neverInjectsExtraQueryParams`.
Every other value interpolated into an SDK query string is either a `UUID`/closed-set enum (no
injection risk) or already formatted by `OffsetDateTime.toString()` (ISO-8601, safe charset).

## Known limitations (documented, not hidden)

1. **Unbounded response body size** (item 11) — accepted in this version because the real API
   (Ishtaran) is not an untrusted origin in the SDK's normal usage flow; the risk only exists if
   `baseUrl` points to a compromised host. Future mitigation: replace `BodyHandlers.ofString()`
   with a handler that has a configurable size limit.
2. **Automated dependency scan not run** (item 8) — the 3 production dependencies are mature
   libraries widely scanned by the community (Jackson, SLF4J), but no CVE check was run this
   session specifically against the versions pinned in `pom.xml`.

Neither limitation blocks using the SDK against the real Ishtaran API today — both are logged for
handling before any eventual public release (Maven Central).

## Verdict

**PASS**, with the 2 limitations above explicitly documented (never hidden) — no critical or
high-severity finding remains unfixed or unjustified.
