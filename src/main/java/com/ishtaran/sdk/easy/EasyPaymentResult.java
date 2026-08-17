package com.ishtaran.sdk.easy;

import com.ishtaran.sdk.model.enums.PaymentIntentStatus;
import com.ishtaran.sdk.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Combina o estado de Transaction + Payment Intent em um objeto único de leitura fácil — expõe os
 * IDs reais do Core ({@code transactionId}/{@code paymentIntentId}) para debugging, mesmo em modo
 * fácil (regra do brief).
 */
public record EasyPaymentResult(
        UUID transactionId,
        UUID paymentIntentId,
        TransactionStatus transactionStatus,
        PaymentIntentStatus paymentIntentStatus,
        String depositAddress,
        BigDecimal amount) {
}
