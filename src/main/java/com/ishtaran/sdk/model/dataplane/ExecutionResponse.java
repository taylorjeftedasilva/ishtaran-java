package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.ExecutionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * PROMPT 5 section 9 (G.7) -- {@code GET /v1/organizations/{organizationId}/executions}. The only
 * real remediation for an {@code AWAITING_SIGNATURE}/{@code OVERDUE} Execution is the settlement
 * flow (non-custodial model -- there is no cancel path for an Execution).
 */
public record ExecutionResponse(
        UUID executionId,
        UUID transactionId,
        UUID organizationId,
        ExecutionStatus status,
        OffsetDateTime preparedAt,
        OffsetDateTime gracePeriodExpiresAt,
        OffsetDateTime executedAt,
        UUID settlementId) {
}
