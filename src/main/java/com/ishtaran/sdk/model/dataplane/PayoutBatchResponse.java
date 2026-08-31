package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.PayoutBatchStatus;
import com.ishtaran.sdk.model.enums.PayoutBatchTrigger;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * SPEC-025 — a batched payout execution grouping N beneficiary obligations under a single
 * NetworkExecutionQuote. This SDK slice only ever creates batches with {@code trigger = MANUAL}
 * (the public route accepts no other trigger yet — THRESHOLD_CROSSED/SCHEDULED exist in the
 * domain but aren't reachable through the public API today).
 */
public record PayoutBatchResponse(
        UUID payoutBatchId,
        UUID organizationId,
        UUID environmentId,
        UUID assetNetworkId,
        PayoutBatchTrigger trigger,
        PayoutBatchStatus status,
        List<PayoutBatchObligationResponse> obligations,
        NetworkExecutionQuoteSnapshotResponse networkExecutionQuoteSnapshot,
        UUID signingRequestId,
        OffsetDateTime createdAt) {
}
