# Getting Started

## 1. Add the dependency

```xml
<dependency>
    <groupId>com.ishtaran</groupId>
    <artifactId>ishtaran-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

## 2. Build the client

```java
import com.ishtaran.sdk.IshtaranClient;
import com.ishtaran.sdk.config.Environment;

var client = IshtaranClient.builder()
        .apiKey(System.getenv("ISHTARAN_API_KEY"))
        .environment(Environment.SANDBOX)
        .build();
```

`Environment.SANDBOX` resolves to the real public Sandbox automatically — no `.baseUrl(...)`
needed. `Environment.PRODUCTION` doesn't yet have a known real URL (infrastructure not
provisioned — see [`CONFIGURATION.md`](CONFIGURATION.md)); use `.baseUrl(...)` explicitly if you
ever need to point at a self-hosted Local/other instance.

## 3. Check a balance (Easy Mode)

```java
var balance = client.getBalance(accountId, assetNetworkId);
System.out.println("Available: " + balance.available());
```

## 4. Receive a payment (Easy Mode)

```java
var payment = client.receivePayment(organizationId, applicationId, payerAccountId,
        recipientAccountId, assetNetworkId, new BigDecimal("100"));

System.out.println("Deposit address: " + payment.depositAddress());

// Waits until the Payment Intent leaves PENDING/PARTIALLY_PAID — never waits forever.
var finished = client.waitForPayment(payment.transactionId(), payment.paymentIntentId(),
        Duration.ofMinutes(10), Duration.ofSeconds(5));
```

## 5. Withdraw with a visible Network Fee (Easy Mode)

```java
var withdrawal = client.withdraw(organizationId, accountId, assetNetworkId,
        new BigDecimal("50"), "TDestinationAddressReal", null);

System.out.println("You receive " + withdrawal.estimatedRecipientAmount()
        + " (network fee: " + withdrawal.estimatedNetworkFee() + ")");
```

## 6. Or use Core directly

```java
var account = client.accounts().get(accountId);
var transactions = client.transactions().get(transactionId);
var quote = client.withdrawals().quote(organizationId, accountId, destinationId, assetNetworkId, amount);
```

## Next steps

- [`AUTHENTICATION.md`](AUTHENTICATION.md) — when to use an API Key vs. Member login
- [`EASY_MODE.md`](EASY_MODE.md) — complete criteria for when to use each layer
- [`ERROR_HANDLING.md`](ERROR_HANDLING.md) — handling `IshtaranError`
- [`examples/`](../examples/) — 11 numbered examples, from quickstart to full Sandbox
