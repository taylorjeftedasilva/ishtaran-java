package com.ishtaran.sdk.easy;

import com.ishtaran.sdk.model.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Result of Easy Mode {@code withdraw()} — never hides the Network Fee (explicit rule from the
 * brief). Exposes the real Core {@code withdrawalId} for debugging, even in easy mode.
 * {@code estimatedNetworkFee} is deprecated and always {@code null} under SelfCustody; see
 * {@link #networkExecutionCost} for the real network cost.
 */
public record EasyWithdrawResult(
        UUID withdrawalId,
        BigDecimal requestedAmount,
        // Deprecated -- vestigial under SelfCustody, always null. Use networkExecutionCost.
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        BigDecimal networkExecutionCost,
        WithdrawalStatus status) {
}
