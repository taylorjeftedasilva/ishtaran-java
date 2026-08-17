package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * {@code CreatePaymentIntent} devolve só {@code { paymentIntentId }} — o {@code depositAddress}
 * real (gerado sincronamente na criação) só é exposto pelo GET dedicado em seguida, nunca no corpo
 * do POST (mesmo comportamento documentado em {@code examples/quickstart-node/index.js} real).
 */
public record CreatePaymentIntentResult(UUID paymentIntentId) {
}
