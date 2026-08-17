# Features

Derivado de [`SDK_FEATURE_MATRIX.md`](../../SDK_FEATURE_MATRIX.md) (fonte de verdade compartilhada
entre as 4 linguagens). Este arquivo é o resumo específico do Java — para o detalhe rota-a-rota,
veja o documento raiz.

## Estado (2026-08-17)

114 de 119 capacidades rastreadas `DONE` para Java. As 5 restantes:

| Capacidade | Estado |
|---|---|
| `examples/` (11 exemplos numerados) | Em progresso nesta sessão |
| Documentação completa | Concluída nesta sessão (este arquivo faz parte dela) |
| `SECURITY_REVIEW.md` | Em progresso nesta sessão |

Core API: **93/93 operações reais implementadas** (16 de 16 módulos em escopo). Easy Mode: 100%
(`payments.*`, `withdraw`, `getBalance`, `verifyWebhookSignature`). Cross-cutting: 100% (config,
auth, erros, retry, idempotência, paginação, enums forward-compatible, segurança/redação, logging
opt-in, waitFor seguro, empacotamento validado).

## Parity com outras linguagens

TypeScript/Python/Go ainda não começaram — `SDK_FEATURE_MATRIX.md` marca `BLOCKED` para as 3, per a
regra não-negociável do brief do SDK Program (nenhuma linguagem começa antes de Java fechar com
`PASS`).
