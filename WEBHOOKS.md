# Webhooks

## Protocolo real (extraído byte a byte do backend)

Headers enviados em toda entrega: `X-Webhook-Signature` (HMAC-SHA256 hex minúsculo),
`X-Webhook-Timestamp` (Unix seconds, string), `X-Webhook-Delivery-Id` (GUID).

```
signedContent = "{timestamp}.{rawBodyJson}"
signature     = lowercase_hex(HMAC_SHA256(key = UTF8(endpointSecret), message = UTF8(signedContent)))
```

## Verificação (sem chamada HTTP)

```java
@PostMapping("/webhooks/ishtaran")
public ResponseEntity<Void> handleWebhook(
        @RequestBody String rawBody,
        @RequestHeader("X-Webhook-Signature") String signature,
        @RequestHeader("X-Webhook-Timestamp") String timestamp) {

    boolean valid = client.verifyWebhookSignature(rawBody, signature, timestamp, endpointSecret);
    if (!valid) {
        return ResponseEntity.status(401).build();
    }
    // processar o evento...
    return ResponseEntity.ok().build();
}
```

**Use sempre o `rawBody` exatamente como recebido** — nunca o JSON re-serializado pelo seu
framework. Reserialização pode mudar espaçamento/ordem de campos e quebrar a comparação de
assinatura, mesmo com o payload semanticamente idêntico.

## O que a verificação garante

- Comparação em **tempo constante** (`MessageDigest.isEqual`, nunca `String.equals`).
- Validação de `timestamp` contra replay (tolerância padrão de 5 minutos, configurável).
- Nunca loga o `endpointSecret`.

## Configurar/gerenciar endpoints (Core, requer Member JWT)

```java
var endpoint = client.webhookEndpoints().create(organizationId, "https://myapp.com/webhooks/ishtaran");
// endpoint.secret() -- guarde AGORA, nunca recuperável depois

client.webhookEndpoints().rotateSecret(endpoint.webhookEndpointId()); // novo secret, mesmo invariante
client.webhookEndpoints().deactivate(endpoint.webhookEndpointId());
```

Gestão de endpoint hoje só funciona com Member JWT (não aceita API Key — lacuna real da API, ver
`SDK_CAPABILITY_SPEC.md` §12.4). A verificação de assinatura em si não depende disso — funciona com
qualquer client, sem autenticação alguma, já que é cálculo local.
