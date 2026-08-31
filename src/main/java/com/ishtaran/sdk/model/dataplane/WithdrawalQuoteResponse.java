package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Mirrors {@code Withdrawals.Contracts.Responses.WithdrawalQuoteResponse} exactly. SPEC-026
 * Descoberta 7/8 — {@code estimatedNetworkFee} is deprecated and always {@code null} under
 * SelfCustody (the only reachable path today, DEC-041): the beneficiary always receives the full
 * {@code requestedAmount}, never {@code amount - fee}. {@code networkExecutionCost} is the new
 * source of truth for network cost (SPEC-NETEXEC-001). {@code preview quote != execution quote}
 * -- {@code request()} always re-quotes from zero via {@code EnsureViableAsync}, never reuses
 * this response as a price guarantee.
 */
public record WithdrawalQuoteResponse(
        UUID accountId,
        UUID withdrawalDestinationId,
        UUID assetNetworkId,
        BigDecimal requestedAmount,
        // Deprecated -- vestigial under SelfCustody, always null. Use networkExecutionCost.
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        BigDecimal networkExecutionCost,
        OffsetDateTime expiresAt) {
}
