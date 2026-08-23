package com.ishtaran.sdk.easy;

import com.ishtaran.sdk.model.enums.PaymentIntentStatus;
import com.ishtaran.sdk.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Combines Transaction + Payment Intent state into a single easy-to-read object — exposes the
 * real Core IDs ({@code transactionId}/{@code paymentIntentId}) for debugging, even in easy
 * mode (rule from the brief).
 */
public record EasyPaymentResult(
        UUID transactionId,
        UUID paymentIntentId,
        TransactionStatus transactionStatus,
        PaymentIntentStatus paymentIntentStatus,
        String depositAddress,
        BigDecimal amount) {
}
