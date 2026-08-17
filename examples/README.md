# Ishtaran Java SDK — Exemplos

11 exemplos numerados, código real (nunca pseudocódigo), compilados e verificados neste módulo
Maven contra o SDK real (`com.ishtaran:ishtaran-java`).

| # | Arquivo | Demonstra |
|---|---|---|
| 01 | `Example01Auth.java` | Quickstart mínimo — API key → client → primeira chamada |
| 02 | `Example02CreateAccount.java` | Criar Account (Core) |
| 03 | `Example03ReceivePaymentEasy.java` | Receber pagamento (Easy Mode) + `waitForPayment` |
| 04 | `Example04CreateTransactionCore.java` | Criar Transaction com participantes (Core) |
| 05 | `Example05PaymentIntentCore.java` | Payment Intent + `depositAddress` real (Core) |
| 06 | `Example06Settlement.java` | Liquidar Transaction + resumo (Core) |
| 07 | `Example07WithdrawalQuote.java` | Cotar saque, Network Fee sempre visível (Core) |
| 08 | `Example08Withdrawal.java` | Executar saque (Easy Mode) + `waitFor` |
| 09 | `Example09Ledger.java` | Saldo + Ledger Entries com paginação real (Core) |
| 10 | `Example10WebhookVerification.java` | Verificação de assinatura — **único 100% executável sem API real** |
| 11 | `Example11Sandbox.java` | Faucet + confirmação simulada (Sandbox) |

## Rodando

Todos exigem uma instância real da API Ishtaran rodando, exceto o `10`:

```bash
export ISHTARAN_API_KEY=...
export ISHTARAN_ORGANIZATION_ID=...
# ... demais variáveis por exemplo, ver o topo de cada arquivo

mvn compile exec:java -Dexec.mainClass=com.ishtaran.examples.Example01Auth
```

O `Example10WebhookVerification` roda sem nenhuma variável de ambiente real (cálculo local, sem
chamada HTTP):

```bash
mvn compile exec:java -Dexec.mainClass=com.ishtaran.examples.Example10WebhookVerification
```

## Pré-requisitos não cobertos aqui

Criar a Organization/primeiro Member/Asset Network está fora do escopo destes exemplos, pela mesma
razão documentada em `examples/quickstart-node/README.md` (raiz do repositório) — nenhuma dessas
ações é hoje algo que um integrador realiza sozinho via API pública.
