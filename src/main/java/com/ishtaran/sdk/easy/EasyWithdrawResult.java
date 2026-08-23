package com.ishtaran.sdk.easy;

import com.ishtaran.sdk.model.enums.WithdrawalStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Result of Easy Mode {@code withdraw()} — never hides the Network Fee (explicit rule from the
 * brief). Exposes the real Core {@code withdrawalId} for debugging, even in easy mode.
 */
public record EasyWithdrawResult(
        UUID withdrawalId,
        BigDecimal requestedAmount,
        BigDecimal estimatedNetworkFee,
        BigDecimal estimatedRecipientAmount,
        WithdrawalStatus status) {
}
