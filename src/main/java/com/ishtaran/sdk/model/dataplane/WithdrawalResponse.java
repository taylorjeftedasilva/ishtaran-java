package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Espelha {@code Withdrawals.Contracts.Responses.WithdrawalResponse} exatamente. */
public record WithdrawalResponse(
        UUID withdrawalId,
        UUID organizationId,
        UUID accountId,
        UUID withdrawalDestinationId,
        UUID assetNetworkId,
        BigDecimal amount,
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        BigDecimal finalNetworkFee,
        BigDecimal finalRecipientAmount,
        WithdrawalStatus status,
        UUID entryGroupId,
        String technicalReference,
        OffsetDateTime createdAt) {
}
