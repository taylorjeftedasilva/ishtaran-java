package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code CreateTransaction} returns {@code { transactionId }} (real anonymous object, see TransactionsEndpoints.cs). */
public record CreateTransactionResult(UUID transactionId) {
}
