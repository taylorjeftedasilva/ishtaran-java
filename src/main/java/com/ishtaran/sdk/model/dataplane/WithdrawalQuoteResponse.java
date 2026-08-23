package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Mirrors {@code Withdrawals.Contracts.Responses.WithdrawalQuoteResponse} exactly. Never hides
 * the Network Fee -- {@code estimatedNetworkFee} is always exposed (explicit rule from the brief).
 */
public record WithdrawalQuoteResponse(
        UUID accountId,
        UUID withdrawalDestinationId,
        UUID assetNetworkId,
        BigDecimal requestedAmount,
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        OffsetDateTime expiresAt) {
}
