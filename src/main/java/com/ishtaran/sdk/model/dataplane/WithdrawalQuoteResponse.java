package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Espelha {@code Withdrawals.Contracts.Responses.WithdrawalQuoteResponse} exatamente. Nunca esconde
 * a Network Fee — {@code estimatedNetworkFee} é sempre exposta (regra explícita do brief).
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
