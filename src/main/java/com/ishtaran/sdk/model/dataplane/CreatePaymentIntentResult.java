package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * {@code CreatePaymentIntent} returns only {@code { paymentIntentId }} — the real
 * {@code depositAddress} (generated synchronously on creation) is only exposed by the dedicated
 * GET that follows, never in the POST body (same behavior documented in the real
 * {@code examples/quickstart-node/index.js}).
 */
public record CreatePaymentIntentResult(UUID paymentIntentId) {
}
