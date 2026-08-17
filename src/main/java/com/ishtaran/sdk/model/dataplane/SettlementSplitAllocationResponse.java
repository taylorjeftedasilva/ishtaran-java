package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.SplitAllocationStatus;
import com.ishtaran.sdk.model.enums.SplitRetentionReason;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** {@code retentionReason} só é não-nulo quando {@code status == RETAINED}. */
public record SettlementSplitAllocationResponse(
        UUID splitAllocationId,
        UUID participantId,
        UUID accountId,
        BigDecimal amount,
        SplitAllocationStatus status,
        SplitRetentionReason retentionReason,
        OffsetDateTime releasedAt) {
}
