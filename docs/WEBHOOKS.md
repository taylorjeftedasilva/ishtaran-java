# Webhooks

## Real protocol (extracted byte-for-byte from the backend)

Headers sent on every delivery: `X-Webhook-Signature` (lowercase hex HMAC-SHA256),
`X-Webhook-Timestamp` (Unix seconds, string), `X-Webhook-Delivery-Id` (GUID).

```
signedContent = "{timestamp}.{rawBodyJson}"
signature     = lowercase_hex(HMAC_SHA256(key = UTF8(endpointSecret), message = UTF8(signedContent)))
```

## Verification (no HTTP call)

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
    // process the event...
    return ResponseEntity.ok().build();
}
```

**Always use the `rawBody` exactly as received** — never the JSON re-serialized by your
framework. Re-serialization can change spacing/field order and break the signature comparison,
even with a semantically identical payload.

## What the verification guarantees

- **Constant-time** comparison (`MessageDigest.isEqual`, never `String.equals`).
- `timestamp` validated against replay (default 5-minute tolerance, configurable).
- Never logs the `endpointSecret`.

## Configuring/managing endpoints (Core, requires Member JWT)

```java
var endpoint = client.webhookEndpoints().create(organizationId, "https://myapp.com/webhooks/ishtaran");
// endpoint.secret() -- save it NOW, never retrievable again later

client.webhookEndpoints().rotateSecret(endpoint.webhookEndpointId()); // new secret, same invariant
client.webhookEndpoints().deactivate(endpoint.webhookEndpointId());
```

Endpoint management today only works with Member JWT (doesn't accept an API Key — a real API gap,
see `SDK_CAPABILITY_SPEC.md` §12.4). Signature verification itself doesn't depend on this — it
works with any client, with no authentication at all, since it's a local calculation.
