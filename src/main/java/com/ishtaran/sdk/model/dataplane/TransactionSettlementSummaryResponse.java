package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@code remainingReservedAmount} = ainda reservado, não liquidado nem reembolsado;
 * {@code retainedAmount} = soma de {@code SplitAllocationStatus.RETAINED} entre todos os
 * Settlements da Transaction (DEC-019/DEC-022 — visibilidade real de valor pendente/retido).
 */
public record TransactionSettlementSummaryResponse(
        UUID transactionId,
        BigDecimal settledAmount,
        BigDecimal refundedAmount,
        BigDecimal remainingReservedAmount,
        BigDecimal retainedAmount) {
}
