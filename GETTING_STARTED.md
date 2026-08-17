# Getting Started

## 1. Adicione a dependência

```xml
<dependency>
    <groupId>com.ishtaran</groupId>
    <artifactId>ishtaran-java</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2. Construa o client

```java
import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

var client = IshtaranClient.builder()
        .apiKey(System.getenv("ISHTARAN_API_KEY"))
        .environment(Environment.LOCAL)
        .build();
```

`Environment.SANDBOX`/`PRODUCTION` ainda não têm uma URL real conhecida (infraestrutura não
provisionada — ver [`CONFIGURATION.md`](CONFIGURATION.md)); use `.baseUrl(...)` explicitamente
quando apontar para um desses ambientes.

## 3. Consulte um saldo (Easy Mode)

```java
var balance = client.getBalance(accountId, assetNetworkId);
System.out.println("Available: " + balance.available());
```

## 4. Receba um pagamento (Easy Mode)

```java
var payment = client.receivePayment(organizationId, applicationId, payerAccountId,
        recipientAccountId, assetNetworkId, new BigDecimal("100"));

System.out.println("Deposit address: " + payment.depositAddress());

// Espera até o Payment Intent sair de PENDING/PARTIALLY_PAID — nunca espera para sempre.
var finished = client.waitForPayment(payment.transactionId(), payment.paymentIntentId(),
        Duration.ofMinutes(10), Duration.ofSeconds(5));
```

## 5. Saque com Network Fee visível (Easy Mode)

```java
var withdrawal = client.withdraw(organizationId, accountId, assetNetworkId,
        new BigDecimal("50"), "TDestinationAddressReal", null);

System.out.println("Você recebe " + withdrawal.estimatedRecipientAmount()
        + " (taxa de rede: " + withdrawal.estimatedNetworkFee() + ")");
```

## 6. Ou use o Core diretamente

```java
var account = client.accounts().get(accountId);
var transactions = client.transactions().get(transactionId);
var quote = client.withdrawals().quote(organizationId, accountId, destinationId, assetNetworkId, amount);
```

## Próximos passos

- [`AUTHENTICATION.md`](AUTHENTICATION.md) — quando usar API Key vs. login de Member
- [`EASY_MODE.md`](EASY_MODE.md) — critério completo de quando usar cada camada
- [`ERROR_HANDLING.md`](ERROR_HANDLING.md) — tratando `IshtaranError`
- [`examples/`](examples/) — 11 exemplos numerados, do quickstart ao Sandbox completo
