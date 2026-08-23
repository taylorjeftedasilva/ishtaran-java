package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code ReserveTransactionBalance} returns {@code { entryGroupId }} (real anonymous object, see TransactionsEndpoints.cs). */
public record ReserveTransactionResult(UUID entryGroupId) {
}
