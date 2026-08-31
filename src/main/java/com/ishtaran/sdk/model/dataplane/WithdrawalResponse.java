package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.NetworkExecutionCostStatus;
import com.ishtaran.sdk.model.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Espelha {@code Withdrawals.Contracts.Responses.WithdrawalResponse} exatamente. SPEC-026
 * Descoberta 8 — {@code estimatedNetworkFee}/{@code finalNetworkFee} are deprecated and always
 * {@code null} under SelfCustody (the only reachable path today, DEC-041): the beneficiary
 * always receives the full {@code amount}, never {@code amount - fee}. {@code signingRequestId}
 * is populated only under SelfCustody, once there's something to sign (same role as
 * {@code SettlementResponse.signingRequestId}). {@code networkExecutionCost}/
 * {@code networkExecutionCostStatus} are the new source of truth for network cost, via
 * {@code NetworkExecutionCostSettlementService} (SPEC-NETEXEC-002); both {@code null} before a
 * network cost has been reserved yet.
 */
public record WithdrawalResponse(
        UUID withdrawalId,
        UUID organizationId,
        UUID environmentId,
        UUID accountId,
        UUID withdrawalDestinationId,
        UUID assetNetworkId,
        BigDecimal amount,
        // Deprecated -- vestigial under SelfCustody, always null. Use networkExecutionCost.
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        // Deprecated -- vestigial under SelfCustody, always null. Use networkExecutionCost.
        BigDecimal finalNetworkFee,
        BigDecimal finalRecipientAmount,
        WithdrawalStatus status,
        UUID entryGroupId,
        String technicalReference,
        UUID signingRequestId,
        BigDecimal networkExecutionCost,
        NetworkExecutionCostStatus networkExecutionCostStatus,
        OffsetDateTime createdAt) {
}
