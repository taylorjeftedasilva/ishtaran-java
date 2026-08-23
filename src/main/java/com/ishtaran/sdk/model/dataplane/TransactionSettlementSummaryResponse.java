package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@code remainingReservedAmount} = still reserved, neither settled nor refunded;
 * {@code retainedAmount} = sum of {@code SplitAllocationStatus.RETAINED} across all
 * Settlements of the Transaction (DEC-019/DEC-022 -- real visibility into pending/retained amounts).
 */
public record TransactionSettlementSummaryResponse(
        UUID transactionId,
        BigDecimal settledAmount,
        BigDecimal refundedAmount,
        BigDecimal remainingReservedAmount,
        BigDecimal retainedAmount) {
}
