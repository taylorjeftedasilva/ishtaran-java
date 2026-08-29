package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.SettlementStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SettlementResponse(
        UUID settlementId,
        UUID transactionId,
        UUID organizationId,
        UUID applicationId,
        UUID assetNetworkId,
        BigDecimal grossAmount,
        BigDecimal platformFeeAmount,
        BigDecimal distributableAmount,
        BigDecimal feePercentageApplied,
        UUID platformRevenueAccountId,
        UUID pricingPolicyId,
        SettlementStatus status,
        UUID entryGroupId,
        /**
         * DEC-037 — populated only under SelfCustody, once {@code SelfCustodySettlementExecutionStrategy}
         * creates a real {@code SigningRequest} (never under ManagedCustody, never before there's
         * something to sign). Fetch it via {@code client.signingRequests().get(signingRequestId)} to
         * sign locally.
         */
        UUID signingRequestId,
        List<SettlementSplitAllocationResponse> splitAllocations,
        OffsetDateTime createdAt,
        OffsetDateTime executedAt) {
}
