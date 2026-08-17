package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code CreateTransaction} devolve {@code { transactionId }} (objeto anônimo real, ver TransactionsEndpoints.cs). */
public record CreateTransactionResult(UUID transactionId) {
}
