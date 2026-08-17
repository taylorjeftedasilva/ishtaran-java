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
        List<SettlementSplitAllocationResponse> splitAllocations,
        OffsetDateTime createdAt,
        OffsetDateTime executedAt) {
}
