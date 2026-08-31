package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.NetworkCostPayer;
import com.ishtaran.sdk.model.enums.NetworkResourceSource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * SPEC-NETEXEC-001 (brief section 13) — mirror of
 * {@code ExecutionCustody.Domain.ValueObjects.NetworkExecutionQuote}. {@code nativeExecutionCost}/
 * {@code authorizedNativeCost} are always in the RESOURCE asset's native units
 * ({@code resourceAssetNetworkId} or {@code assetNetworkId}); {@code totalCharged} is always in
 * {@code quoteCurrency} (the CHARGED asset) — {@code totalCharged = (nativeExecutionCost * fx) +
 * safetyBuffer + replenishmentRequirement + conversionOverhead}. {@code authorizedNativeCost} is
 * the number actually reserved for execution (&gt;= the sum of every physical operation's cost,
 * INC-18) — never compare a caller-supplied estimate directly against {@code nativeExecutionCost}
 * alone.
 */
public record NetworkExecutionQuoteResponse(
        String network,
        NetworkExecutionPlanResponse plan,
        NetworkResourceEstimateResponse estimatedResources,
        BigDecimal nativeExecutionCost,
        UUID resourceAssetNetworkId,
        String quoteCurrency,
        BigDecimal fx,
        BigDecimal safetyBuffer,
        NetworkResourceSource resourceSource,
        BigDecimal replenishmentRequirement,
        BigDecimal conversionOverhead,
        OffsetDateTime expiresAt,
        BigDecimal totalCharged,
        NetworkCostPayer networkCostPayer,
        BigDecimal authorizedNativeCost) {
}
