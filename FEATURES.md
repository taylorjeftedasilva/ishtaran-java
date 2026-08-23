# Features

Derived from the real API contract (see the [API Reference](https://ishtaran.com/docs/api/ishtaran-api)),
the same source of truth shared across the 4 languages.

## Status (2026-08-17)

114 of 119 tracked capabilities `DONE` for Java. The 5 remaining:

| Capability | Status |
|---|---|
| `examples/` (11 numbered examples) | In progress this session |
| Complete documentation | Completed this session (this file is part of it) |
| `SECURITY_REVIEW.md` | In progress this session |

Core API: **93/93 real operations implemented** (16 of 16 modules in scope). Easy Mode: 100%
(`payments.*`, `withdraw`, `getBalance`, `verifyWebhookSignature`). Cross-cutting: 100% (config,
auth, errors, retry, idempotency, pagination, forward-compatible enums, security/redaction,
opt-in logging, safe waitFor, validated packaging).

## Parity with other languages

TypeScript/Python/Go haven't started yet — `SDK_FEATURE_MATRIX.md` marks `BLOCKED` for the 3, per
the SDK Program brief's non-negotiable rule (no language starts before Java closes with `PASS`).
