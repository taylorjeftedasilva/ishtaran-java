package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RefundResponse(
        UUID refundId,
        UUID transactionId,
        UUID organizationId,
        BigDecimal amount,
        String reason,
        RefundStatus status,
        UUID entryGroupId,
        OffsetDateTime createdAt,
        OffsetDateTime executedAt) {
}
